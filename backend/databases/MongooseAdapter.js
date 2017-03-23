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
  },

  selectUserByFacebookIdAndAccessToken: function(id, token, callback) {
    User.findOne({facebookId: id, accessToken: token}, callback);
  },

  updateUserAccessToken: function(user, token, callback) {
    user.accessToken = token;
    user.save(callback);
  },

  updateUserCrushByFacebookIdAndAccessToken: function(id, token, crushId, crushName, callback) {
    User.findOne({facebookId: id, accessToken: token}, function(err, user) {
      if (err) {
        return callback(err);
      }
      if (!user) {
        return callback(null, null);
      }
      user.crushId = crushId;
      user.crushName = crushName;
      user.dateCrushUpdated = Date.now();
      user.save(callback);
    });
  }

};