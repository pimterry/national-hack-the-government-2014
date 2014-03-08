import sys, re
import mechanize, BeautifulSoup
from py2neo import neo4j

def get_all_election_results():
  election_urls = get_all_election_page_urls()
  for election_url in election_urls:
    for region_url in get_all_region_urls(election_url):
      for result in get_all_overall_results(region_url):
        yield result

def get_all_election_page_urls():
  browser = mechanize.Browser()
  browser.open("http://www.politicsresources.net/area/uk/edates.htm")
  election_links = [link for link in browser.links()
                    if re.match("(\d\d\d\d): ", link.text)]

  election_is_after_1930 = lambda text: int(re.match("(\d\d\d\d): ", text).groups()[0]) > 1930
  detailed_election_links = [link for link in election_links
                                  if election_is_after_1930(link.text)]

  return [link.absolute_url for link in detailed_election_links]

def get_all_region_urls(election_url):
  browser = mechanize.Browser()
  browser.open(election_url)
  region_links = [link for link in browser.links()
                       if "\xe2\x80\x94" in link.text]

  return [link.absolute_url for link in region_links]

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
          "area": str(area.string),
          "year": year,
          "results": filter(lambda x: x is not None,
                            [build_result(row) for row in results_rows])
      }

def build_result(results_row):
  cell_values = [str(cell.string) for cell in results_row.findAll("td")]
  try:
    return {
        "person": cell_values[0],
        "party": cell_values[1],
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

