var express = require('express');
var router = express.Router();

router.get('/crush', function(req, res) {
  res.json({
    love: 'You',
    status: 'Success'
  });
});

module.exports = router;