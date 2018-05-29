#!groovy
 
import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule
import groovy.json.JsonSlurper
import hudson.plugins.active_directory.*

 
def instance = Jenkins.getInstance()

def env = System.getenv()
def inputFile = new File(env['CONFIG_VARS'])


// **Uncomment the lines below to add a non-domain admin account**

def user = env['USERNAME']
def pass = env['PASSWORD']

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(user, pass)

// **Uncomment the lines below to add a domain account**
// 
// def hudsonRealm = new  ActiveDirectorySecurityRealm(
//     InputJSON.active_directory.domain, 
//     InputJSON.active_directory.site, 
//     InputJSON.active_directory.bindName, 
//     InputJSON.active_directory.bindPassword, 
//     InputJSON.active_directory.server, 
//     GroupLookupStrategy.CHAIN
// )

instance.setSecurityRealm(hudsonRealm)
 
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
instance.setAuthorizationStrategy(strategy)
instance.save()
 
Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

instance.setSecurityRealm(hudsonRealm)