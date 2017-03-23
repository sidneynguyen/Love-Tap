var express = require('express');
var mongoose = require('mongoose');

module.exports = {
  User: {},
  connect: function() {
    mongoose.Promise = global.Promise;
    mongoose.connect('mongodb://localhost:27017/lovetap');
    require('../models/user');
    User = mongoose.model('User');
  },
  
  insertUser: function(user, callback) {
    var newUser = new User(user);
    newUser.save(user, callback);
  },

  selectUserByFacebookId: function(id, callback) {
    User.findOne({facebookId: id}, callback);
  }

};