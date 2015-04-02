/**
 * Module Dependencies
 */
// ...
// e.g.
// var _ = require('lodash');
// var mysql = require('node-mysql');
// ...
var ESConnection = require('elasticsearch').Client({
  host: 'http://localhost:9200'
});



/**
 * waterline-elasticsearch
 *
 * Most of the methods below are optional.
 *
 * If you don't need / can't get to every method, just implement
 * what you have time for.  The other methods will only fail if
 * you try to call them!
 *
 * For many adapters, this file is all you need.  For very complex adapters, you may need more flexiblity.
 * In any case, it's probably a good idea to start with one file and refactor only if necessary.
 * If you do go that route, it's conventional in Node to create a `./lib` directory for your private submodules
 * and load them at the top of the file with other dependencies.  e.g. var update = `require('./lib/update')`;
 */
module.exports = (function () {


  // You'll want to maintain a reference to each connection
  // that gets registered with this adapter.
  var connections = {};



  // You may also want to store additional, private data
  // per-connection (esp. if your data store uses persistent
  // connections).
  //
  // Keep in mind that models can be configured to use different databases
  // within the same app, at the same time.
  //
  // i.e. if you're writing a MariaDB adapter, you should be aware that one
  // model might be configured as `host="localhost"` and another might be using
  // `host="foo.com"` at the same time.  Same thing goes for user, database,
  // password, or any other config.
  //
  // You don't have to support this feature right off the bat in your
  // adapter, but it ought to get done eventually.
  //

  var adapter = {

    // Set to true if this adapter supports (or requires) things like data types, validations, keys, etc.
    // If true, the schema for models using this adapter will be automatically synced when the server starts.
    // Not terribly relevant if your data store is not SQL/schemaful.
    //
    // If setting syncable, you should consider the migrate option,
    // which allows you to set how the sync will be performed.
    // It can be overridden globally in an app (config/adapters.js)
    // and on a per-model basis.
    //
    // IMPORTANT:
    // `migrate` is not a production data migration solution!
    // In production, always use `migrate: safe`
    //
    // drop   => Drop schema and data, then recreate it
    // alter  => Drop/add columns as necessary.
    // safe   => Don't change anything (good for production DBs)
    //
    syncable: true,


    // Default configuration for connections
    defaults: {
			// For example, MySQLAdapter might set its default port and host.
      // port: 3306,
      // host: 'localhost',
      // schema: true,
      // ssl: false,
      // customThings: ['eh']
      host: 'localhost',
      port: 9200
    },



    /**
     *
     * This method runs when a model is initially registered
     * at server-start-time.  This is the only required method.
     *
     * @param  {[type]}   connection [description]
     * @param  {[type]}   collection [description]
     * @param  {Function} cb         [description]
     * @return {[type]}              [description]
     */
    registerConnection: function(connection, collections, cb) {

      if(!connection.identity) return cb(new Error('Connection is missing an identity.'));
      if(connections[connection.identity]) return cb(new Error('Connection is already registered.'));

      // Add in logic here to initialize connection
      // e.g. connections[connection.identity] = new Database(connection, collections);
      connections[connection.identity] = connection;

      /*new ESConnection(connection, function(err, es) {
        if(err) return cb(err);
      });*/

      cb();
    },


    /**
     * Fired when a model is unregistered, typically when the server
     * is killed. Useful for tearing-down remaining open connections,
     * etc.
     *
     * @param  {Function} cb [description]
     * @return {[type]}      [description]
     */
    // Teardown a Connection
    teardown: function (conn, cb) {
      console.log('teardown');
      if (typeof conn == 'function') {
        cb = conn;
        conn = null;
      }
      if (!conn) {
        connections = {};
        return cb();
      }
      if(!connections[conn]) return cb();
      delete connections[conn];
      cb();
    },


    // Return attributes
    describe: function (connection, collection, cb) {
			// Add in logic here to describe a collection (e.g. DESCRIBE TABLE logic)
      console.log('describe');
      return cb();
    },

    /**
     *
     * REQUIRED method if integrating with a schemaful
     * (SQL-ish) database.
     *
     */
    define: function (connection, collection, definition, cb) {
			// Add in logic here to create a collection (e.g. CREATE TABLE logic)
      console.log('define');
      //var exists = ESConnection.indices.exists({index: 'sails-es'});
      //console.log("Exists:");
      //console.log(exists);
      //if(!exists)
      ESConnection.indices.create({index:'sails-es'});
      console.log('/define');
      return cb();
    },

    /**
     *
     * REQUIRED method if integrating with a schemaful
     * (SQL-ish) database.
     *
     */
    drop: function (connection, collection, relations, cb) {
			// Add in logic here to delete a collection (e.g. DROP TABLE logic)
      console.log('drop');
      ESConnection.indices.delete({index:'sails-es'});
			return cb();
    },

    /**
     *
     * REQUIRED method if users expect to call Model.find(), Model.findOne(),
     * or related.
     *
     * You should implement this method to respond with an array of instances.
     * Waterline core will take care of supporting all the other different
     * find methods/usages.
     *
     */
    find: function (connection, collection, options, cb) {
      console.log('______________');
      console.log('find');
      console.log('connection:');
      console.log(connection);
      console.log('_________________________________________________________');
      console.log('collection:');
      console.log(collection);
      console.log('_________________________________________________________');
      console.log('where');
      console.log(JSON.stringify(options.where));

      var query;
      if(options.where)
        query = {match: options.where};
      else
        query = {match_all: {}};

      ESConnection.search({
        index: 'sails-es',
        body: {
          query : query
        }
        //query: {
        //  term: JSON.stringify(options.where)
        //}
      }, function(error, response) {
        if(error) return cb(error);
        cb(null, response.hits.hits);
      });
    },

    create: function (connection, collection, values, cb) {
      console.log('______________');
      console.log('create');
      console.log('connection:');
      console.log(connection);
      console.log('_________________________________________________________');
      console.log('collection:');
      console.log(collection);
      console.log('_________________________________________________________');
      console.log('values');
      console.log(values);
      ESConnection.create({
        index:'sails-es',
        type: collection,
        body: values
      }, function(error, response) {
        if(error) return cb(error);
        cb(null, response);
      });
    },

    update: function (connection, collection, options, values, cb) {
      console.log('update');
      ESConnection.update({
        index: 'sails-es',
        type: 'string',
        id: '1',
        body: {
          name: 'asd'
        }
      }, function(error, response) {
        console.log("UPDATE");
        console.log("RESPONSE: " + response);
        console.log("ERROR: " + error);
        cb(response);
      });
      return cb();
    },

    destroy: function (connection, collection, options, values, cb) {
      console.log('destroy');
      ESConnection.delete({
        index: 'sails-es',
        type: 'string',
        id: '1'
      }, function(error, response) {
        console.log("DESTROY");
        console.log("RESPONSE: " + response);
        console.log("ERROR: " + error);
        cb(response);
      });
      return cb();
    }

    /*

    // Custom methods defined here will be available on all models
    // which are hooked up to this adapter:
    //
    // e.g.:
    //
    foo: function (collectionName, options, cb) {
      return cb(null,"ok");
    },
    bar: function (collectionName, options, cb) {
      if (!options.jello) return cb("Failure!");
      else return cb();
      destroy: function (connection, collection, options, values, cb) {
       return cb();
     }

    // So if you have three models:
    // Tiger, Sparrow, and User
    // 2 of which (Tiger and Sparrow) implement this custom adapter,
    // then you'll be able to access:
    //
    // Tiger.foo(...)
    // Tiger.bar(...)
    // Sparrow.foo(...)
    // Sparrow.bar(...)


    // Example success usage:
    //
    // (notice how the first argument goes away:)
    Tiger.foo({}, function (err, result) {
      if (err) return console.error(err);
      else console.log(result);

      // outputs: ok
    });

    // Example error usage:
    //
    // (notice how the first argument goes away:)
    Sparrow.bar({test: 'yes'}, function (err, result){
      if (err) console.error(err);
      else console.log(result);

      // outputs: Failure!
    })




    */




  };


  // Expose adapter definition
  return adapter;

})();
