var express = require('express');
var router = express.Router();
var passport = require('passport');
var FacebookTokenStrategy = require('passport-facebook-token');
var secrets = require('../secrets');
var db = require('../databases/MongooseAdapter');

router.get('/', function(req, res) {
  res.json({
    isAuthenticated: req.isAuthenticated()
  });
});

router.post('/facebook/token',
  passport.authenticate('facebook-token'),
  function (req, res) {
    // do something with req.user
    var user = {
      _id: req.user._id,
      facebookId: req.user.facebookId,
      dateCreated: req.user.dateCreated,
      isAuthenticated: "" + req.isAuthenticated()
    }
    console.log(user);
    res.json(user);
  }
);

passport.use(new FacebookTokenStrategy({
    clientID: secrets.clientID,
    clientSecret: secrets.clientSecret
  }, function(accessToken, refreshToken, profile, done) {
    db.insertUser({facebookId: profile.id}, function(err, user) {
      return done(err, user);
    });
  }
));

passport.serializeUser(function(user, done) {
  done(null, user._id);
});

passport.deserializeUser(function(id, done) {
  db.selectUserById(id, function(err, user) {
    done(err, user);
  });
});

module.exports = router;