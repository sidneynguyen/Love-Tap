var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');

var app = express();

var api = require('./routes/api');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));

app.use('/api', api);

var port = 3000;
app.listen(port, function() {
  console.log('Server started on port ' + port);
});