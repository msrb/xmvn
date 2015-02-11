/*-
 * Copyright (c) 2013-2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fedoraproject.xmvn.dependency.impl;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.StringUtils;

import org.fedoraproject.xmvn.artifact.Artifact;
import org.fedoraproject.xmvn.artifact.DefaultArtifact;
import org.fedoraproject.xmvn.model.AbstractModelVisitor;
import org.fedoraproject.xmvn.utils.ArtifactTypeRegistry;

/**
 * @author Mikolaj Izdebski
 */
class BuildDependencyVisitor
    extends AbstractModelVisitor
{
    private static final Set<Artifact> commonPlugins = new LinkedHashSet<>();
    static
    {
        // FIXME: don't hardcode this

        // Default lifecycle mappings for packaging "jar"
        commonPlugins.add( new DefaultArtifact( "org.apache.maven.plugins", "maven-resources-plugin", "jar", "SYSTEM" ) );
        commonPlugins.add( new DefaultArtifact( "org.apache.maven.plugins", "maven-compiler-plugin", "jar", "SYSTEM" ) );
        commonPlugins.add( new DefaultArtifact( "org.apache.maven.plugins", "maven-surefire-plugin", "jar", "SYSTEM" ) );
        commonPlugins.add( new DefaultArtifact( "org.apache.maven.plugins", "maven-jar-plugin", "jar", "SYSTEM" ) );

        // Called by XMvn directly
        commonPlugins.add( new DefaultArtifact( "org.apache.maven.plugins", "maven-javadoc-plugin", "jar", "SYSTEM" ) );
    }

    private final DefaultDependencyExtractionResult result;

    private static final List<String> buildScopes = Arrays.asList( null, "compile", "provided", "test" );

    private static final List<String> runtimeScopes = Arrays.asList( null, "compile", "runtime" );

    public BuildDependencyVisitor( DefaultDependencyExtractionResult result )
    {
        this.result = result;
    }

    @Override
    public void visitParent( Parent parent )
    {
        result.addDependencyArtifact( new DefaultArtifact( parent.getGroupId(), parent.getArtifactId(), "pom",
                                                           parent.getVersion() ) );
    }

    @Override
    public void visitDependency( Dependency dependency )
    {
        if ( !buildScopes.contains( dependency.getScope() ) )
            return;

        result.addDependencyArtifact( ArtifactTypeRegistry.getDefaultRegistry().createTypedArtifact( dependency.getGroupId(),
                                                                                                     dependency.getArtifactId(),
                                                                                                     dependency.getType(),
                                                                                                     dependency.getClassifier(),
                                                                                                     dependency.getVersion(),
                                                                                                     dependency.getOptional() != null ? dependency.isOptional() : null ) );
    }

    @Override
    public void visitBuildExtension( Extension extension )
    {
        result.addDependencyArtifact( new DefaultArtifact( extension.getGroupId(), extension.getArtifactId(),
                                                           extension.getVersion() ) );
    }

    @Override
    public void visitBuildPlugin( Plugin plugin )
    {
        String groupId = plugin.getGroupId();
        String artifactId = plugin.getArtifactId();
        String version = plugin.getVersion();
        if ( StringUtils.isEmpty( groupId ) )
            groupId = "org.apache.maven.plugins";
        if ( StringUtils.isEmpty( version ) )
            version = Artifact.DEFAULT_VERSION;

        Artifact pluginArtifact = new DefaultArtifact( groupId, artifactId, version );
        Artifact versionlessPluginArtifact = pluginArtifact.setVersion( Artifact.DEFAULT_VERSION );

        if ( !commonPlugins.contains( versionlessPluginArtifact ) )
            result.addDependencyArtifact( pluginArtifact );
    }

    @Override
    public void visitBuildPluginDependency( Dependency dependency )
    {
        if ( !runtimeScopes.contains( dependency.getScope() ) )
            return;

        result.addDependencyArtifact( ArtifactTypeRegistry.getDefaultRegistry().createTypedArtifact( dependency.getGroupId(),
                                                                                                     dependency.getArtifactId(),
                                                                                                     dependency.getType(),
                                                                                                     dependency.getClassifier(),
                                                                                                     dependency.getVersion(),
                                                                                                     dependency.getOptional() != null ? dependency.isOptional() : null ) );
    }
}
