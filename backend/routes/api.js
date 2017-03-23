var express = require('express');
var router = express.Router();
var db = require('../databases/MongooseAdapter');

router.post('/me/crush', function(req, res) {
  var facebookId = req.body.facebookId;
  var accessToken = req.body.accessToken;
  db.selectUserByFacebookIdAndAccessToken(facebookId, accessToken, function(err, user) {
    res.json(user);
  });
});

router.post('/crush', function(req, res) {
  var facebookId = req.body.facebookId;
  var accessToken = req.body.accessToken;
  var crushId = req.body.crushId;
  var crushName = req.body.crushName;
  db.updateUserCrushByFacebookIdAndAccessToken(facebookId, accessToken, crushId, crushName, function(err, user) {
    if (err) {
      res.send(err);
    }
    res.json(user);
  });
});

module.exports = router;