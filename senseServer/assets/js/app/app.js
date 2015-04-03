var app = angular.module('main', []);

app.controller('mainController', ['$scope', '$log', function($scope, $log) {
  $scope.sensors = [];

  io.socket.on('sensor', function(obj) {
    if(obj.verb === 'created') {
      $log.info("CREATED");
      $log.info(obj);
    }
  });

  io.socket.get('/sensor', function(resData, jwres) {
    $log.info(resData);
    for(var r in resData) {
      $scope.sensors.push({name:resData[r]._source.name, value:resData[r]._source.value});
    }
    $scope.$apply();
  });
}]);
