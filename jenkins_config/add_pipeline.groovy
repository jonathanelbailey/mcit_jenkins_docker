import hudson.util.PersistedList
import jenkins.model.Jenkins
import jenkins.branch.*
import jenkins.plugins.git.*
import org.jenkinsci.plugins.workflow.multibranch.*
import com.cloudbees.hudson.plugins.folder.*

def env = System.getenv()
// Bring some values in from ansible using the jenkins_script modules wierd "args" approach (these are not gstrings)
String folderName = env['FOLDER_NAME']
String jobName = env['JOB_NAME']
String jobScript = env['JOB_SCRIPT']
String gitRepo = env['GIT_REPO']
String gitRepoName = env['GIT_REPO_NAME']
String credentialsId = env['CREDENTIALS_ID']

Jenkins jenkins = Jenkins.getInstance() // saves some typing

// Get the folder where this job should be
def folder = jenkins.getItem(folderName)
// Create the folder if it doesn't exist
if (folder == null) {
  folder = jenkins.createProject(Folder, folderName)
}

// Multibranch creation/update
WorkflowMultiBranchProject mbp
def item = folder.getItem(jobName)
if ( item != null ) {
  // Update case
  mbp = (WorkflowMultiBranchProject) item
} else {
  // Create case
  mbp = folder.createProject(WorkflowMultiBranchProject, jobName)
}

// Configure the script this MBP uses
mbp.getProjectFactory().setScriptPath(jobScript)

// Add git repo
String id = null
String remote = gitRepo
String includes = "*"
String excludes = ""
boolean ignoreOnPushNotifications = false
GitSCMSource gitSCMSource = new GitSCMSource(id, remote, credentialsId, includes, excludes, ignoreOnPushNotifications)
BranchSource branchSource = new BranchSource(gitSCMSource)

// Disable triggering build
NoTriggerBranchProperty noTriggerBranchProperty = new NoTriggerBranchProperty()

// Can be used later to not trigger/trigger some set of branches
//NamedExceptionsBranchPropertyStrategy.Named nebrs_n = new NamedExceptionsBranchPropertyStrategy.Named("change-this", noTriggerBranchProperty)

// Add an example exception
BranchProperty defaultBranchProperty = null;
NamedExceptionsBranchPropertyStrategy.Named nebrs_n = new NamedExceptionsBranchPropertyStrategy.Named("change-this", defaultBranchProperty)
NamedExceptionsBranchPropertyStrategy.Named[] nebpsa = [ nebrs_n ]

BranchProperty[] bpa = [noTriggerBranchProperty]
NamedExceptionsBranchPropertyStrategy nebps = new NamedExceptionsBranchPropertyStrategy(bpa, nebpsa)

branchSource.setStrategy(nebps)

// Remove and replace?
PersistedList sources = mbp.getSourcesList()
sources.clear()
sources.add(branchSource)