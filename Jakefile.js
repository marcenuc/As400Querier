
/*jslint node: true */
/*jshint node: true */
/*global desc: false, task: false, fail: false, complete: false, namespace: false,
         setTimeout: false */

var spawn = require('child_process').spawn,
  util = require('util');

function exec(command, args, reader) {
  'use strict';
  var cmd = spawn(command, args);

  cmd.stdout.on('data', function (data) {
    reader(null, data.toString());
  });
  cmd.stderr.on('data', function (data) {
    reader(null, null, data.toString());
  });
  cmd.on('exit', function (code) {
    reader(code);
  });
}

function console_exec(command, args, success) {
  'use strict';

  exec(command, args, function (err, stdout, stderr) {
    if (stdout) {
      process.stdout.write(stdout);
    }
    if (stderr) {
      process.stderr.write(command + ':' + stderr);
    }
    if (err) {
      throw err;
    } else if (err === 0 && success) {
      success();
    }
  });
}


namespace('query', function () {
  'use strict';
  var baseOpts = ['-jar', __dirname + '/target/boutique-connector-0.0.1-SNAPSHOT-jar-with-dependencies.jar'];

  desc('Query for scalarini');
  task('scalarini', function () {
    console_exec('java', baseOpts.concat('scalarini'));
  });

});


desc('Check coding style');
task('lint', function () {
  'use strict';
  var glob = require('glob'),
    patterns = arguments.length > 0 ?
      [].slice.apply(arguments) :
      ['*.js', 'test/*/*.js'];

  function lint(fileName) {
    console_exec('jshint', [fileName, '--config', '.jshintrc'], function () {
      console_exec('jslint', ['--indent=2', '--es5', '--nomen', fileName]);
    });
  }

  patterns.forEach(function (globPattern) {
    glob.glob(globPattern, function (err, fileNames) {
      if (err) {
        fail(util.inspect(err));
      }
      fileNames.forEach(lint);
    });
  });
});

namespace('build', function () {
  'use strict';

  desc('Build binary package');
  task('package', function () {
    console_exec('mvn', ['package']);
  });
});

namespace('build', function () {
  'use strict';

  desc('Build binary package');
  task('package', function () {
    console_exec('mvn', ['package']);
  });
});
