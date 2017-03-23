var express = require('express');
var router = express.Router();
var db = require('../databases/MongooseAdapter');

router.post('/me/crush', function(req, res) {
  var facebookId = req.body.facebookId;
  var accessToken = req.body.accessToken;
  db.selectUserByFacebookIdAndAccessToken(facebookId, accessToken, function(err, user) {
    if (err) {
      return res.send(err);
    }
    if (!user) {
      return res.json({err: 'User not found'});
    }
    db.selectUserByFacebookId(user.crushId, function(err, crush) {
      if (err) {
        return res.send(err);
      }
      if (!crush) {
        return res.json(user);
      }
      user.me = crush.crushId == user.facebookId;
      console.log(user);
      res.json({
        crushId: user.crushId,
        crushName: user.crushName,
        me: user.me
      });
    });
  });
});

router.post('/crush', function(req, res) {
  var facebookId = req.body.facebookId;
  var accessToken = req.body.accessToken;
  var crushId = req.body.crushId;
  var crushName = req.body.crushName;
  db.selectUserByFacebookIdAndAccessToken(facebookId, accessToken, function(err, user) {
    if (err) {
      return res.send(err);
    }
    if (!user) {
      return res.json({err: 'User not found'});
    }
    var date = new Date(Date.now());
    if (!user.dateCrushUpdated) {
      db.updateUserCrushByFacebookIdAndAccessToken(facebookId, accessToken, crushId, crushName, function(err, user) {
        if (err) {
          return res.send(err);
        }
        res.json(user);
      });
    } else if (date.getUTCDay() > user.dateCrushUpdated.getUTCDay() && date.getUTCHours() >= user.dateCrushUpdated.getUTCHours()) {
      db.updateUserCrushByFacebookIdAndAccessToken(facebookId, accessToken, crushId, crushName, function(err, user) {
        if (err) {
          return res.send(err);
        }
        res.json(user);
      });
    } else {
      res.json({err: 'Must wait 24 hours'});
    }
  });
  
});

module.exports = router;