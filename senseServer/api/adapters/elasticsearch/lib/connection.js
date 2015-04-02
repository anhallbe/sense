var elasticsearch = require('elasticsearch');

module.exports = (function() {
	var db = false;

	function connect(connection) {
		if(!db) {
			var path = 'http://' + connection.host + ':' + connection.port;
			db = true;

			var client = elasticsearch.Client({
				host: path
			});

			client.ping({
				requestTimeout: 1000,
				hello: "elasticsearch!"
			}, function(error) {
				if (error) {
					console.log('An error has occurred when trying to connect to ElasticSearch');
					throw error;
				}
				else {
					console.log('Connection established to Elastic Search at: ' + path);
				}
			});
		}
		return client;
	}

	return {
		connect: connect
	};
})();
