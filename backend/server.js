/**
 * File Name: server.js
 * Authors: Sidney Nguyen
 * Date Created: 3/27/17
 */

var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var session = require('express-session');
var passport = require('passport');
var FacebookTokenStrategy = require('passport-facebook-token');
var secrets = require('./secrets');
var db = require('./databases/MongooseAdapter');
db.connect(secrets.databaseURI);

var app = express();

var api = require('./routes/api');
var auth = require('./routes/auth');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(session({
  secret: secrets.sessionSecret,
  saveUninitialized: true,
  resave: true
}));
app.use(passport.initialize());
app.use(passport.session());

app.use('/api', api);
app.use('/auth', auth);

var port = 3000;
app.listen(port, function() {
  console.log('Server started on port ' + port);
});