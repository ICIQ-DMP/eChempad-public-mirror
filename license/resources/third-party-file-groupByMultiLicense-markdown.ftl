<#-- Format artifact "name (groupId:artifactId:version - url)" -->
<#function artifactFormat artifact>
    <#if artifact.name?index_of('Unnamed') &gt; -1>
        <#return artifact.artifactId + " | " + artifact.groupId + ":" + artifact.artifactId + " | " + artifact.version + " | " + (artifact.url!"no url defined")>
    <#else>
        <#return artifact.name + " | " + artifact.groupId + ":" + artifact.artifactId + " | " + artifact.version + " | " + (artifact.url!"no url defined") >
    </#if>
</#function>

<#-- Create a key from provided licenses list, ordered alphabetically: "license A, license B, license C" -->
<#function licensesKey licenses>
  <#local result = "">
  <#list licenses?sort as license>
      <#local result=result + ", " + license>
  </#list>
  <#return result?substring(2)>
</#function>

<#-- Aggregate dependencies map for generated license key (support for multi-license) and convert artifacto to string -->
<#function aggregateLicenses dependencies>
    <#assign aggregate = {}>
    <#list dependencyMap as entry>
        <#assign project = artifactFormat(entry.getKey())/>
        <#assign licenses = entry.getValue()/>
        <#assign key = licensesKey(licenses)/>
        <#if aggregate[key]?? >
            <#assign replacement = aggregate[key] + [project] />
            <#assign aggregate = aggregate + {key:replacement} />
        <#else>
          <#assign aggregate = aggregate + {key:[project]} />
        </#if>
    </#list>
    <#return aggregate>
</#function>
# ioChem-BD third party license summary

The following is a list of all Java third-party dependencies used inside the ioChem-BD project, grouped by their license type. You can review each one on [SPDX webpage](https://spdx.org/licenses/).

The full text for each listed license can be found in *./third-party* folder.

## License list

<#if dependencyMap?size == 0>
  The project has no dependencies.
<#else>
<#assign aggregate = aggregateLicenses(dependencyMap)>
    <#-- Print sorted aggregate licenses -->
    <#list aggregate?keys?sort as licenses>
        <#assign projects = aggregate[licenses]/>

###  ${licenses}:

Name | id | version | url
---- | --- | --------- | ----
        <#list projects?sort as project>
${project}
        </#list>
    </#list>
</#if>

