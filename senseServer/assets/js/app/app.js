var app = angular.module('main', []);

app.controller('mainController', ['$scope', '$log', function($scope, $log) {
  $scope.sensors = [];

  $scope.search = function() {
    $log.info("search " + $scope.query);
    io.socket.get('/sensor/search?q=' + $scope.query, function(resData, jwres) {
      $scope.sensors = [];
      for(var r in resData) {
        $scope.sensors.push({name:resData[r]._source.name, value:resData[r]._source.value, id:resData[r]._id, time:resData[r]._source.createdAt});
      }
      $scope.$apply();
    });
  };

  $scope.addSensor = function() {

  };

  io.socket.on('sensor', function(obj) {
    if(obj.verb === 'created') {
      $log.info("CREATED");
      $log.info(obj);
    }
  });

  io.socket.get('/sensor', function(resData, jwres) {
    $log.info(resData);
    for(var r in resData) {
      $scope.sensors.push({name:resData[r]._source.name, value:resData[r]._source.value, id:resData[r]._id, time:resData[r]._source.createdAt});
    }
    $scope.$apply();
  });
}]);
