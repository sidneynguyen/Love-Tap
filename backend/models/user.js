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
  dateCrushUpdated: {
    type: Number,
    default: 0
  }
});

mongoose.model('User', userSchema);