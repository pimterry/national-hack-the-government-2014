import sys

if __name__ == "__main__":
  if len(sys.argv) < 2:
    print("Usage: python scrape-constituency-results.py NEO4J_URL")
    sys.exit(1)

  neo4j_url = sys.argv[1]

