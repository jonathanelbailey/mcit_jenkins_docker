import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.domains.Domain
import hudson.util.Secret
import jenkins.model.JenkinsLocationConfiguration
import org.jenkinsci.plugins.github.GitHubPlugin
import org.jenkinsci.plugins.github.config.GitHubPluginConfig
import org.jenkinsci.plugins.github.config.GitHubServerConfig
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import groovy.json.JsonSlurper

def env = System.getenv()

// configure JENKINS_URL
JenkinsLocationConfiguration jenkinsLocationConfiguration = JenkinsLocationConfiguration.get()
jenkinsLocationConfiguration.adminAddress = env['GITHUB_ADMIN_ADDRESS']
jenkinsLocationConfiguration.setUrl(env['JENKINS_URL'])
jenkinsLocationConfiguration.save()

// configure credentials

def credentialsID = env['CREDENTIALS_ID']
def domain = Domain.global()
def store = jenkins.model.Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()
if (store.getCredentials(domain).find { it.id == credentialsID } == null) {
  def secretText = new StringCredentialsImpl(
          CredentialsScope.GLOBAL,
          credentialsID,
          InputJSON.credentials.description as String,
          Secret.fromString(InputJSON.credentials.password ?: 'DUMMY')
  )
  store.addCredentials(domain, secretText)
}

// configure github plugin
GitHubPluginConfig pluginConfig = GitHubPlugin.configuration()
GitHubServerConfig serverConfig = new GitHubServerConfig(InputJSON.credentials.credentialsID)
pluginConfig.setConfigs([serverConfig])
pluginConfig.save()