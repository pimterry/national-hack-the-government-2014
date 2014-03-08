from itertools import *
from py2neo import neo4j

def update_year_relationships():
  db = neo4j.GraphDatabaseService()
  years = sorted(db.find("year"), key=lambda year: int(year["name"]))

  alternate = lambda a, b: chain(*zip(a, b))
  
  forward_path = alternate(repeat("next"), years[1:])
  years[0].get_or_create_path(*forward_path)
 
  backward_path = alternate(repeat("previous"), reversed(years[:-1]))
  years[-1].get_or_create_path(*backward_path)

  return years

if __name__ == "__main__":
  update_year_relationships()
