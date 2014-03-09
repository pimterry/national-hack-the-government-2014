import requests, urllib
from py2neo import neo4j

def get_mp_photo(constituency):
  response = requests.get("http://www.theyworkforyou.com/api/getMP",
               params={"constituency": constituency})
  return response.json()["image"]

