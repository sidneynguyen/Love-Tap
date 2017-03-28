/**
 * File Name: api.js
 * Authors: Sidney Nguyen
 * Date Created: 3/27/17
 */

var express = require('express');
var router = express.Router();
var db = require('../databases/MongooseAdapter');

const ERR_USER_NOT_FOUND = 420;
const ERR_DATABASE = 1337;
const ERR_24_HOURS_NOT_PASSED = 666;

router.post('/get/time', function(req, res) {
  var facebookId = req.body.facebookId;
  var accessToken = req.body.accessToken;
  db.selectUserByFacebookIdAndAccessToken(facebookId, accessToken, function(err, user) {
    if (err) {
      console.log(err);
      return res.json({err: ERR_DATABASE});
    }
    if (!user) {
      return res.json({err: ERR_USER_NOT_FOUND});
    }
    var date = Date.now();
    var canUpdate = user.dateCrushUpdated + 86400000 - date <= 0;
    res.json({
      canUpdate: canUpdate
    });
  });
});

router.post('/get/crush', function(req, res) {
  var facebookId = req.body.facebookId;
  var accessToken = req.body.accessToken;
  db.selectUserByFacebookIdAndAccessToken(facebookId, accessToken, function(err, user) {
    if (err) {
      console.log(err);
      return res.json({err: ERR_DATABASE});
    }
    if (!user) {
      return res.json({err: ERR_USER_NOT_FOUND});
    }
    db.selectUserByFacebookId(user.crushId, function(err, crush) {
      if (err) {
        console.log(err);
        return res.json({err: ERR_DATABASE});
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

router.post('/select/crush', function(req, res) {
  var facebookId = req.body.facebookId;
  var accessToken = req.body.accessToken;
  var crushId = req.body.crushId;
  var crushName = req.body.crushName;
  db.selectUserByFacebookIdAndAccessToken(facebookId, accessToken, function(err, user) {
    if (err) {
      console.log(err);
      return res.json({err: ERR_DATABASE});
    }
    if (!user) {
      return res.json({err: ERR_USER_NOT_FOUND});
    }
    var date = Date.now();
    if (user.dateCrushUpdated + 86400000 - date <= 0) {
      db.updateUserCrushByFacebookIdAndAccessToken(facebookId, accessToken, crushId, crushName, function(err, user) {
        if (err) {
          console.log(err);
          return res.json({err: ERR_DATABASE});
        }
        res.json({
          crushId: user.crushId,
          crushName: user.crushName
        });
      });
    } else {
      res.json({err: ERR_24_HOURS_NOT_PASSED});
    }
  });
});

router.post('/clear/crush', function(req, res) {
  var facebookId = req.body.facebookId;
  var accessToken = req.body.accessToken;
  db.deleteUserCrushByFacebookIdAndAccessToken(facebookId, accessToken, function(err, user) {
    if (err) {
      console.log(err);
      return res.json({err: ERR_DATABASE});
    }
    if (!user) {
      return res.json({err: ERR_USER_NOT_FOUND});
    }
    return res.json({
      crushId: null,
      crushName: null
    });
  });
});

module.exports = router;