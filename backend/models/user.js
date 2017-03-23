var mongoose = require('mongoose');

var userSchema = mongoose.Schema({
  facebookId: String,
  dateCreated: {
    type: Date,
    default: Date.now
  }
});

mongoose.model('User', userSchema);