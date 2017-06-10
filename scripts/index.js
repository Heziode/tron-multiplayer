#!/usr/bin/env node

const MAX_CPU_CORE_DIVIDER = 2; // We will take half the CPUs of the machines, so we divide by 2
const MAX_DATA_BEFORE_SAVE = 10; // Maximum number of data that is stored before saving it to a file
const DEFAULT_RESULT_DIR = './result'; // Default directory where results are stored

var os = require('os');
var fs = require('fs');
var child = require('child_process');
var argv = require('yargs')
    .usage('Usage: $0 <command> [options]')
    .example('$0 -x tron.jar -s 5 -c 3 -n 2 -e 10', 'Execute 10 games were single player have a depth of 5 against 2 players which have a depth of 3')

    // Executable jar path
    .option('x', {
        alias: 'execute',
        demand: true,
        describe: 'jar file to execute',
        type: 'string'
    })
    .nargs('x', 1)

    // Solo player search depth
    .option('s', {
        alias: 'single-player-depth',
        demand: true,
        describe: 'depth research of single player',
        type: 'number'
    })
    .nargs('s', 1)

    // Coalition research depth
    .option('c', {
        alias: 'coalition-depth',
        demand: true,
        describe: 'depth research of coalition',
        type: 'number'
    })
    .nargs('c', 1)

    // Number of players in the coalition
    .option('n', {
        alias: 'coalition-number',
        demand: true,
        describe: 'number of players in coalition',
        type: 'number'
    })
    .nargs('n', 1)

    // Number of experiments
    .option('e', {
        alias: 'experience',
        demand: true,
        describe: 'number of experimentations',
        type: 'number'
    })
    .nargs('e', 1)

    // Starting index of experiments
    .option('is', {
        alias: 'experience-start-index',
        demand: false,
        default: 1,
        describe: 'start index of experience',
        type: 'number'
    })
    .nargs('is', 1)

    // End-of-experimentation index
    .option('ie', {
        alias: 'experience-end-index',
        demand: false,
        describe: 'end index of experience',
        type: 'number'
    })
    .nargs('ie', 1)

    // Execution in ssh
    .option('ssh', {
        demand: false,
        describe: 'ssh address',
        type: 'string'
    })
    .nargs('ssh', 1)

    // Results directory
    .option('r', {
        alias: 'result-dir',
        demand: false,
        default: DEFAULT_RESULT_DIR,
        describe: 'results directory',
        type: 'string'
    })
    .nargs('r', 1)

    // Defined whether to leave the program saved only the results in files or not
    .option('cs', {
        alias: 'custom-save',
        demand: false,
        describe: 'Allow to save result in file every x experiment',
        type: 'boolean'
    })

    // Minimum data before saving to a file
    .option('mbs', {
        alias: 'max-before-save',
        demand: false,
        default: MAX_DATA_BEFORE_SAVE,
        describe: 'Minimum data before saving to a file',
        type: 'int'
    })
    .nargs('mbs', 1)

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
    if (isNaN(argv.s) || (argv.s < 1)) {
        addErrorMessage('Single player depth need to be a number greater or equal to 1');
    }
    if (isNaN(argv.c) || (argv.c < 1)) {
        addErrorMessage('Coalition depth need to be a number greater or equal to 1');
    }
    if (isNaN(argv.n) || (argv.n < 1)) {
        addErrorMessage('Number of player in coalition need to be a number greater or equal to 1');
    }
    if (isNaN(argv.e) || (argv.e < 1)) {
        addErrorMessage('Number of experimentation need to be a number greater or equal to 1');
    }
    else {
        if (isNaN(argv.is) || (argv.is < 1) || (argv.is > argv.e)) {
            addErrorMessage('Number of start experimentation need to be a number greater or equal to 1 and lower than number of experimentation');
        }

        if ((argv.ie !== undefined)) {
            if (isNaN(argv.ie) || (argv.ie < 1) || (argv.ie > argv.e) || (argv.ie < argv.is)) {
                addErrorMessage('Number of end experimentation need to be a number greater or equal to 1 and lower than number of experimentation');
            }
        }
        else {
            argv.ie = argv.e;
        }
    }
    if ((argv.ssh !== undefined) && (argv.ssh.length === 0)) {
        addErrorMessage('SSH address cannot be empty');
    }
    if ((argv.r !== undefined) && (argv.r.length === 0)) {
        addErrorMessage('Result path cannot be empty');
    }
    if ((argv.mbs !== undefined) && ( (isNaN(argv.mbs)) || (argv.mbs < 1) )) {
        addErrorMessage('max-before-save need to be a number greater or equal to 1');
    }
}

/**
 * Retrieve the number of max thread that is allowed for the machine where the script is executed
 * @return {number} Returns the number of children that can be executed simultaneously
 */
function getMaxSimultaneousChilds() {
    var nbCPUCore;

    if (argv.ssh !== undefined) {
        nbCPUCore = child.execSync('ssh ' + argv.ssh + ' "nproc"');
    }
    else {
        nbCPUCore = os.cpus().length;
    }

    return Math.ceil(nbCPUCore / MAX_CPU_CORE_DIVIDER);
}

/**
 * Utility function that adds 0 to get a uniform number format.
 * Retrieved from {@link http://stackoverflow.com/a/8043254}
 * @param number Number of entries
 * @param targetLength Size on which the number is to be represented
 * @return {string} Returns the formatted string
 */
function leftPad(number, targetLength) {
    var output = number + '';
    while (output.length < targetLength) {
        output = '0' + output;
    }
    return output;
}

/**
 * Create a file name with the game's parameters and the number of the experiment
 * @param xpNumber Current experience number
 * @return {string} Returns the name of the file
 */
function getResultFileName(xpNumber) {
    var nbj = leftPad(argv.n + 1, 2);
    var dj = leftPad(argv.s, 2);
    var dc = leftPad(argv.c, 2);
    var nbXp = leftPad(xpNumber, NB_XP.toString().length);

    return nbj + dj + dc + '_' + nbXp;
}

/**
 * Create command to execute
 * @param xpNumber Current experience number
 * @return {string} Returns the command
 */
function getCommand(xpNumber) {
    var command = 'java -Dtron.coal="' + argv.s + ' ' + argv.c + '" -Dtron.nbCoal="1 ' + argv.n + '" ' +
        '-Dtron.random=true -Dtron.result=true -Dtron.gui=false ';
    if (argv.cs) {
        command += '-Dtron.result.print=true ';
    }
    if (argv.ssh !== undefined) {
        command = 'ssh -oStrictHostKeyChecking=no ' + argv.ssh + ' \'cd tron/tron-multijoueur/scripts ; ' + command +
            ' -jar ' + argv.x + '\'';
    }
    else {
        command += '-Dtron.result.name="' + getResultFileName(xpNumber) + '" -jar ' + argv.x;
    }
    return command;
}

/**
 * Performs an experiment
 */
function launchExperience() {
    if (nbXP === END_XP) {
        endProcess();
    }
    if ((nbChild < MAX_SIMULTANEOUS_CHILD) && ((nbXP + nbChild) < END_XP)) {

        var c = child.exec(getCommand(currentXp));

        if (argv.cs !== undefined) {
            c.stdout.on('data', function (chunk) {
                addResult(chunk, nbXP);
            });
        }

        c.on('exit', function () {
            nbChild--;
            nbXP++;
            launchExperience();
        });

        nbChild++;
        currentXp++;
    }
}

/**
 * Ends the application
 */
function endProcess() {
    if (argv.ssh !== null) {
        saveResult(result);
    }
    console.log("Process finished !");
    process.exit();
}

/**
 * Checks the integrity of the data passed as a parameter. The data must end with a '\n'
 * @param data Data to check
 * @return {boolean} Returns TRUE if the data is valid, FALSE otherwise
 */
function checkIntegrity(data) {
    var lastChar = data.slice(-1);
    return lastChar === '\n';
}

/**
 * Adds a data to the results if it is correct, or displays an error message otherwise
 * @param data Data to be added
 * @param nbXP Number of experimentation
 * @see checkIntegrity
 */
function addResult(data, nbXP) {
    if (checkIntegrity(data)) {
        if (nbValidData >= DATA_LIMIT) {
            if (saveResult(result)) {
                result = '';
            }
        }
        result += (nbXP + 1) + ',' + data;
        nbValidData++;
    }
    else {
        console.error('The data from experiment ' + nbXP + ' are not complete');
    }
}

/**
 * Save the result data to a file
 * @return Returns TRUE if a backup was made, FALSE otherwise
 */
function saveResult(result) {
    if (available && (result !== undefined) && (result.length > 0)) {
        available = false;
        var fileName = getResultFileName(startXp) + '-' + leftPad(nbXP, NB_XP.toString().length) + '.csv';
        var dir = DIR.slice(-1) === '/' ? DIR : DIR + '/';

        if (!fs.existsSync(dir)) {
            var r = child.execSync('mkdir -p ' + dir);
        }
        fs.writeFileSync(dir + fileName, result);

        startXp = nbXP + 1;
        nbValidData = 0;
        available = true;
        return true;
    }
    return false;
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

const NB_XP = argv.e;
const MAX_SIMULTANEOUS_CHILD = getMaxSimultaneousChilds();
const DIR = argv.r;
const END_XP = (argv.ie === null) ? argv.e : argv.ie;
const DATA_LIMIT = argv.mbs;

var nbChild = 0;
var nbXP = argv.is - 1;
var currentXp = argv.is;
var result = '';
var nbValidData = 0;
var startXp = currentXp;
var available = true;

console.log("Under processing...");

do {
    launchExperience();
} while ((nbChild < MAX_SIMULTANEOUS_CHILD) && (currentXp < NB_XP));
