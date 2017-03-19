var express = require('express');
var router = express.Router();

router.get('/crush', function(req, res) {
  res.json({
    love: true,
    success: true
  });
});

module.exports = router;