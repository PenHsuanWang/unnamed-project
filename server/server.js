// Create server
const express = require('express');
const app = express();
const http = require('http').Server(app);

const port = process.env.PORT || 3000;

// Serve index.html
const path = require('path');
app.use(express.static(path.join(__dirname, '..', 'dist')));

// Routing

// GET Method
app.get('/api', (req, res) => {
  // Request URL: http://localhost:3000/api
  const arr = [1,2,3]
  res.send(`GET ${arr} from the api`);
});

const bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// POST Method
app.post('/api', function (req, res) {
  // Request URL: http://localhost:3000/api
  // req.body 為前端 POST 來的 {name: 'Yao', login: 'Hello World'}
  const message = req.body;
  console.log(message)
  res.send(`POST ${JSON. stringify(message)} to the api`)
})

// Start server listening process
http.listen(port, () => {
  console.log(`Listening on port ${port}...`);
});
