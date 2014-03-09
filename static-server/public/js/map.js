(function () {
  svg_height = 400.0
  var svg = d3.select('.map')
               .append('svg')
               .attr('width', 330)
               .attr('height', svg_height);

  var map = UK.ElectionMap();
  map.origin({x: 55, y: 375});
  map.fill(function (constituency) {
    return '#'+(0x1000000+(Math.random())*0xffffff)
            .toString(16).substr(1,6)
  });
  map(svg);

  target_height = $(".map").height() 
  zoom_percentage = (target_height/svg_height) * 100
  $(".map svg").css("zoom", zoom_percentage + "%")
})();                 
