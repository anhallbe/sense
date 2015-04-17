/**
* Sensor.js
*
* @description :: TODO: You might write a short summary of how this model works and what it represents here.
* @docs        :: http://sailsjs.org/#!documentation/models
*/

module.exports = {
  connections: ['elasticsearch'],
  attributes: {
    _id: {
      type: 'string',
      primaryKey: true
    },
    name: {
      type: 'string',
      unique: true,
      required: true
    },
    description: {
      type: 'string',
    },
    valueType: {
      type: 'string',
      required: true
    },
    value: {
      type: 'integer'
    }
  }
};
