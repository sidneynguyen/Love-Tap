var mongoose = require('mongoose');

var userSchema = mongoose.Schema({
  facebookId: String,
  accessToken: String,
  crushName: String,
  crushId: String,
  dateCreated: {
    type: Date,
    default: Date.now
  },
  dateCrushUpdated: Date
});

mongoose.model('User', userSchema);