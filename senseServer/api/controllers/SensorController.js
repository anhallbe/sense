/**
 * SensorController
 *
 * @description :: Server-side logic for managing sensors
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

module.exports = {
	search: function(req, res) {
		Sensor.search(req.query.q, function(err, result) {
			if(err)
				console.log("Search error: " + err);
			else {
				console.log("Search successful. Response: ");
				console.log(result);
				return res.send(result);
			}
		});
	}
};
