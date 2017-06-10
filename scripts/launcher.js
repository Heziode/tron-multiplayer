#!/usr/bin/env node
const DEFAULT_RESULT_DIR = './result'; // Default directory where results are stored

var fs = require('fs');
var child = require('child_process');
var argv = require('yargs')
    .usage('Usage: $0 <command> [options]')
    .example('$0 --nbxp 2 --ssd 5 --sed 7 --csd 2 --cminp 2 --cmaxp 3 -s 1', 'Execute a test and start at 1')

    // Executable jar path
    .option('x', {
        alias: 'execute',
        demand: true,
        describe: 'jar file to execute',
        type: 'string'
    })
    .nargs('x', 1)

    // Number of experiments
    .option('nbxp', {
        demand: true,
        describe: 'Number of XP',
        type: 'number'
    })
    .nargs('nbxp', 1)

    // Minimum search depth of solo player
    .option('ssd', {
        alias: 'single-start-depth',
        demand: true,
        describe: 'Min depth of single player',
        type: 'number'
    })
    .nargs('ssd', 1)

    // Maximum search depth of solo player
    .option('sed', {
        alias: 'single-end-depth',
        demand: true,
        describe: 'Max depth of single player',
        type: 'number'
    })
    .nargs('sed', 1)

    // Minimum search depth of coalition
    .option('csd', {
        alias: 'coal-start-depth',
        demand: true,
        describe: 'Min depth of coalition',
        type: 'number'
    })
    .nargs('csd', 1)

    // Minimum number of coalition
    .option('cminp', {
        alias: 'coal-min-players',
        demand: true,
        describe: 'Min number of players in coalition',
        type: 'number'
    })
    .nargs('cminp', 1)

    // Maximum number of coalition
    .option('cmaxp', {
        alias: 'coal-max-players',
        demand: true,
        describe: 'Max number of players in coalition',
        type: 'number'
    })
    .nargs('cmaxp', 1)

    // Starting index of experiments
    .option('s', {
        alias: 'start',
        demand: true,
        describe: 'start index of experiment',
        type: 'number'
    })
    .nargs('s', 1)

    // Number of for experiment
    .option('nbs', {
        alias: 'Number of sample',
        demand: true,
        describe: 'Number of for experiment',
        type: 'number'
    })
    .nargs('nbs', 1)

    // Results Directory
    .option('r', {
        alias: 'result-dir',
        demand: false,
        default: DEFAULT_RESULT_DIR,
        describe: 'results directory',
        type: 'string'
    })
    .nargs('r', 1)

    .help('h')
    .alias('h', 'help')
    .describe('h', 'show this help')
    .locale('en')
    .strict()
    .argv;


var NB_ERROR = 0;
var ERROR_MESSAGE = '';

/**
 * Add an error message to the error variable and increment the number of errors
 * @param message Message to add
 */
function addErrorMessage(message) {
    ERROR_MESSAGE += message + '\n';
    NB_ERROR++;
}

/**
 * Checks if the data entered are correct
 */
function checkInput() {
    if (argv.x.length === 0) {
        addErrorMessage('A path to executable jar cannot be empty');
    }
    else if (!fs.existsSync(argv.x)) {
        addErrorMessage('File "' + argv.x + '" not found');
    }

    if (isNaN(argv.nbxp) || (argv.nbxp < 1)) {
        addErrorMessage('Number of experiment need to be a number greater or equal to 1');
    }

    if (isNaN(argv.ssd) || (argv.ssd < 1)) {
        addErrorMessage('single-start-depth need to be a number greater or equal to 1');
    }
    else if (isNaN(argv.sed) || (argv.sed < argv.ssd)) {
        addErrorMessage('single-end-depth need to be a number greater or equal to 1');
    }

    if (isNaN(argv.csd) || (argv.csd < 1)) {
        addErrorMessage('coal-start-depth need to be a number greater or equal to 1');
    }

    if (isNaN(argv.cminp) || (argv.cminp < 1)) {
        addErrorMessage('coal-min-players need to be a number greater or equal to 1');
    }
    else if (isNaN(argv.cmaxp) || (argv.cmaxp < argv.cminp)) {
        addErrorMessage('coal-max-players need to be a number greater or equal to 1');
    }

    if (isNaN(argv.s) || (argv.s < 1)) {
        addErrorMessage('Sart index of experiment need to be a number greater or equal to 1 and lower than ' + NB_XP);
    }

    if ((argv.r !== undefined) && (argv.r.length === 0)) {
        addErrorMessage('Result path cannot be empty');
    }
}

/**
 * Builds the command to run on the machine to run a test suite on a sample
 * @param singleDepth Solo player search depth
 * @param coalDepth Coalition research depth
 * @param nbInCoal Number of players in the coalition
 * @param startXp Starting index of experiments
 * @return {string} Returns the generated command
 */
function getCommand(singleDepth, coalDepth, nbInCoal, startXp) {
    var endXp = startXp + argv.nbs - 1;
    if (endXp > NB_XP) {
        endXp = NB_XP;
    }
    var nbBeforeSave = 100;
    if ((singleDepth > 5) && (singleDepth < 7)) {
        nbBeforeSave = 25
    }
    else if (singleDepth >= 7) {
        nbBeforeSave = 1
    }
    return './index.js -x ' + argv.x + ' -s ' + singleDepth + ' -c ' + coalDepth + ' -n ' +
        nbInCoal + ' -e ' + NB_XP + ' --cs --is ' + startXp + ' --ie ' + endXp +
        ' --mbs ' + nbBeforeSave + ' -r ' + argv.r;
}

/**
 * Launches an experiment
 */
function launchExperience() {
    for (var singlePlayerDepth = SINGLE_START_DEPTH; singlePlayerDepth <= SINGLE_END_DEPTH; singlePlayerDepth++) {

        for (var nbCoal = COAL_MIN_PLAYERS; nbCoal <= COAL_MAX_PLAYERS; nbCoal++) {

            for (var coalDepth = COAL_START_DEPTH; coalDepth < singlePlayerDepth; coalDepth++) {
                console.log("XP : sd " + singlePlayerDepth + ' cd ' + coalDepth + ' nbc ' + nbCoal);
                child.execSync(getCommand(singlePlayerDepth, coalDepth, nbCoal, argv.s));
            }
        }
    }
}

//////////
// MAIN //
//////////

checkInput();

if (NB_ERROR !== 0) {
    console.error(ERROR_MESSAGE);
    process.exit();
}

// If we are always there, it is that everything is good, we can continue

const NB_XP = argv.nbxp;
const SINGLE_START_DEPTH = argv.ssd;
const SINGLE_END_DEPTH = argv.sed;
const COAL_START_DEPTH = argv.csd;
const COAL_MIN_PLAYERS = argv.cminp;
const COAL_MAX_PLAYERS = argv.cmaxp;

launchExperience();
