dimension_repo = (function () {
  return {
    get_areas: function (callback) {
      results = [
        {
          "name": "Taunton Deane", 
          "party": "LibDem",     
        }
      ];

      area_map = {}
      for (var ii = 0; ii < results.length; ii++) {
        area_map[results[ii].name] = results[ii];
      }

      callback(area_map);
    },
    get_years: function () {
      return [];
    },
    get_politicians: function () {
      return [];
    },
    get_parties: function () {
      return [];
    }
  };
})();

surprise_repo = (function () {
  return {
    get_election_surprise: function(year, area) {
      return 0;
    },
    get_individual_surprise: function(year, area, surname) {
      return 0;
    }
  }
})();
