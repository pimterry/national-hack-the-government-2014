dimension_repo = (function () {
  fix_area_name = function(name) {
    return name.replace(" & ", " and ")
               .replace("Stoke-on-trent", "Stoke-on-Trent")
               .replace("Cities Of London &westminster",
                        "Cities of London and Westminster")
               .replace("Newcastle Upon Tyne",
                        "Newcastle upon Tyne");
  }


  return {
    get_areas: function (callback) {
      $.ajax({
        "url": "http://nhtg-2014-data.herokuapp.com/areas",
        "success": function (result) {
          areas = result.areas;
          area_map = {}
          for (var ii = 0; ii < areas.length; ii++) {
            areas[ii].name = fix_area_name(areas[ii].name)
            area_map[areas[ii].name] = areas[ii];
          }

          special_case_names = {
            "City of Durham": "Durham, City Of"
          }

          callback(area_map);
        }
      });
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
