(function () {
  function draw_map(fill_function) {
    svg_height = 400.0
    $(".map").html("")
    var svg = d3.select('.map')
                 .append('svg')
                 .attr('width', 330)
                 .attr('height', svg_height);

    var map = UK.ElectionMap();
    map.origin({x: 55, y: 375});
    map.fill(fill_function);
    map(svg);

    target_height = $(".map").height() 
    zoom_percentage = (target_height/svg_height) * 100
    $(".map svg").css("zoom", zoom_percentage + "%")
  }

  draw_map(function() { return "#FFFFFF"; });

  dimension_repo.get_areas(function (areas) {
    party_colours = {
      "LibDem": "#FDBB30",
      "Labour": "#DC241f",
      "Conservative": "#0087DC"
    };

    draw_map(function (area) {
      default_colour = "rgba(0,0,0,0.5)"
      if (areas[area]) {
        return party_colours[areas[area].party] || default_colour;
      } else {
        return default_colour;
      }
    });
  });
})();                 
