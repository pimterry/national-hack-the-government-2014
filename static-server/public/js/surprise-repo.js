dimension_repo = (function () {
  return {
    get_areas: function () {
      return [
        {
          "name": "Taunton Deane", 
          "party": "LibDem",     
        }
      ];
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
});

surprise_repo = (function () {
  return {
    get_election_surprise: function(year, area) {
      return 0;
    },
    get_individual_surprise(year, area, surname) {
      return 0;
    }
  }
})();
