import sys, re, urlparse, string
from itertools import *
import mechanize, bs4 as BeautifulSoup
from py2neo import neo4j

def get_all_election_results():
  have_already_seen_1974 = False

  election_urls = get_all_election_page_urls()
  for election_url, election_year in election_urls:
    # We skip the 1974 February election because it was rubbish
    if election_year == 1974 and not have_already_seen_1974:
      have_already_seen_1974 = True
      continue

    # We have to special case these elections, because the data is *totally* different, sigh
    if election_year in (1997, 2001):
      print "Skipping %s for now" % election_year
      continue
    print "Getting election %s" % election_year

    for region_url, region_name in get_all_region_urls(election_url):
      print "Getting region %s" % region_name
      for result in get_all_overall_results(region_url):
        yield result

  for result in get_1997_and_2001_results():
    yield result

def get_all_election_page_urls():
  browser = mechanize.Browser()
  browser.open("http://www.politicsresources.net/area/uk/edates.htm")

  election_links = [(link, int(re.match("(\d\d\d\d): ", link.text).groups()[0]))
                    for link in browser.links()
                    if re.match("(\d\d\d\d): ", link.text)]

  return [(link.absolute_url, date) for (link, date) in election_links
                                    if date > 1930]

def get_all_region_urls(election_url):
  browser = mechanize.Browser()
  response = browser.open(election_url)
  html_soup = BeautifulSoup.BeautifulSoup(response.read())
  region_links = [link for link in html_soup.findAll("a")
                       if u'\u2014' in link.text or "&mdash;" in link.text]

  absolute_url = lambda link: urlparse.urljoin(election_url, link["href"])

  return [(absolute_url(link), unicode(link.text)) for link in region_links]

def get_all_overall_results(page_url):
  browser = mechanize.Browser()
  page = browser.open(page_url)
  html_soup = BeautifulSoup.BeautifulSoup(page.read())

  year = re.search("(\d\d\d\d)", html_soup.title.string).groups()[0]

  # Result tables are all annotated with <!-- result --> comments. Eugh
  is_result_comment = lambda x: (isinstance(x, BeautifulSoup.Comment) and
                                 x.string.strip() == "result")
  result_comments = html_soup.findAll(text=is_result_comment)
  
  for comment in result_comments:
    results_table = comment.findNext("table")
    if results_table:
      area = comment.findNext("b")
      results_rows = results_table.findAll("tr")

      yield {
          "area": string.capwords(area.string),
          "year": year,
          "results": filter(lambda x: x is not None,
                            [build_result(row) for row in results_rows])
      }

def get_1997_and_2001_results():
  browser = mechanize.Browser()
  browser.open("http://www.politicsresources.net/area/uk/mps01.htm")
  region_urls = [link.absolute_url for link in browser.links()
                                   if "constit/" in link.url]
  
  for url in region_urls:
    page = browser.open(url)
    html_soup = BeautifulSoup.BeautifulSoup(page.read())
    area = string.capwords(unicode(html_soup.find("h1").string))
    table_titles = html_soup.findAll("h2")
    
    for title in table_titles:
      table = title.findNext("table")
      if table and "general election" in unicode(title.string).lower():
        print area
        print title.string
        results_rows = table.findAll("tr")
        yield {
            "area": area,
            "year": re.search("(\d\d\d\d)", title.string).groups()[0],
            "results": filter(lambda x: x is not None,
                              [build_result(row) for row in results_rows if row.findAll("td")])
        }



def build_result(results_row):
  cell_values = [unicode(cell_content) for cell_content in results_row.strings
                 if len(cell_content.strip()) > 0]
  try:
    return {
        "person": cell_values[0].strip(),
        "party": string.capwords(cell_values[1]),
        "votes": int(cell_values[2].replace(",", ""))
    }
  except:
    if "unopposed" not in cell_values[2].lower():
      print "Unusual data, ignoring: %s" % (cell_values,)
    return None

def insert_into_neo4j(election_results):
  print("Connecting to Neo4j")
  db = neo4j.GraphDatabaseService()

  print("Inserting results")
  years = NodeDict(db, "year")
  areas = NodeDict(db, "area")
  politicians = NodeDict(db, "politician")
  parties = NodeDict(db, "party")

  for index, election in enumerate(election_results):
    if index > 0 and index % 100 == 0:
      print("Inserted %s single election results" % index)

    election_node, = db.create({})
    election_node.add_labels("election")

    election_node.get_or_create_path("held_in", areas[election["area"]])
    election_node.get_or_create_path("during", years[election["year"]])

    for result in election["results"]:
      person = politicians[result["person"]]
      person.get_or_create_path(("stood_in", {"votes": person["votes"]}), election_node)
      person.get_or_create_path("member_of", parties[result["party"]])

  print("All results inserted")

class NodeDict(dict):

  def __init__(self, db, label):
    self.db = db
    self.label = label

  def __getitem__(self, key):
    try:
      return dict.__getitem__(self, key)
    except KeyError:
      node, = self.db.create({"name": key})
      node.add_labels(self.label)
      self[key] = node
      return node

if __name__ == "__main__":
  election_results = get_all_election_results()
  insert_into_neo4j(election_results)

