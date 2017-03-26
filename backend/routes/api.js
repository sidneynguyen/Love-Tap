var express = require('express');
var router = express.Router();
var db = require('../databases/MongooseAdapter');

router.post('/me/time', function(req, res) {
  var facebookId = req.body.facebookId;
  var accessToken = req.body.accessToken;
  db.selectUserByFacebookIdAndAccessToken(facebookId, accessToken, function(err, user) {
    if (err) {
      return res.send(err);
    }
    if (!user) {
      return res.json({err: 'User not found'});
    }
    var date = Date.now();
    var canUpdate = user.dateCrushUpdated + 86400000 - date <= 0;
    res.json({
      canUpdate: canUpdate
    });
  });
});

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
      if (crush) {
        user.me = crush.crushId == user.facebookId;
      }
      var date = Date.now();
      var timeLeft = user.dateCrushUpdated + 86400000 - date;
      if (timeLeft < 0) {
        timeLeft = 0;
      }
      res.json({
        crushId: user.crushId,
        crushName: user.crushName,
        me: user.me,
        timeLeft: timeLeft
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
    var date = Date.now();
    if (user.dateCrushUpdated + 86400000 - date <= 0) {
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