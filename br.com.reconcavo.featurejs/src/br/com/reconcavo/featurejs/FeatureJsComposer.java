package br.com.reconcavo.featurejs;

import br.com.reconcavo.featurejs.featuretreat.FeatureTreatmentException;
import br.com.reconcavo.featurejs.file.FileManager;
import br.com.reconcavo.featurejs.metrics.FileFeature;
import br.com.reconcavo.featurejs.model.FeatureJsModelBuilder;
import br.com.reconcavo.featurejs.util.CollectionUtil;
import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.builder.ComposerExtensionClass;
import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.SelectableFeature;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class FeatureJsComposer extends ComposerExtensionClass
{
  private static final String PLUGIN_ID = "br.com.reconcavo.featurejs.FeatureJsComposer";
  private static final String PLUGIN_WARNING = "The required bundle br.com.reconcavo.featurejs.FeatureJsComposer is not installed.";
  public static final String COMPOSER_ID = "br.com.reconcavo.featurejs";
  public static final String FEATURE_JS_EXT = "js";
  public static final String FEATURE_HTML_EXT = "html";
  private FileManager fileManager;
  private IFolder tempFolder;
  private IFolder parentFolder;
  private FeatureJsModelBuilder featureJsModelBuilder;
  private static final ArrayList<String[]> TEMPLATES = createTempltes();

  public boolean initialize(IFeatureProject project)
  {
    if (project == null) {
      return Boolean.FALSE.booleanValue();
    }
    super.initialize(project);
    createTempFolder();

    this.featureJsModelBuilder = new FeatureJsModelBuilder(project, this.tempFolder);
    buildFSTModel();

    return Boolean.TRUE.booleanValue();
  }

  private void createTempFolder()
  {
    this.parentFolder = this.featureProject.getProject().getFolder(".tmp");
    if (!this.parentFolder.exists()) {
      try {
        this.parentFolder.create(Boolean.TRUE.booleanValue(), Boolean.TRUE.booleanValue(), null);
        this.parentFolder.refreshLocal(0, null);
      } catch (CoreException e) {
        FeatureJsCorePlugin.getDefault().logError(e);
      }
    }

    this.tempFolder = this.parentFolder.getFolder("FSTModel");
    if (!this.tempFolder.exists())
      try {
        this.tempFolder.create(Boolean.TRUE.booleanValue(), Boolean.TRUE.booleanValue(), null);
        this.tempFolder.refreshLocal(0, null);
      } catch (CoreException e) {
        FeatureJsCorePlugin.getDefault().logError(e);
      }
  }

  public void performFullBuild(IFile config)
  {
    if (!isPluginInstalled("br.com.reconcavo.featurejs.FeatureJsComposer")) {
      generateWarning("The required bundle br.com.reconcavo.featurejs.FeatureJsComposer is not installed.");
    }
    initialize(CorePlugin.getFeatureProject(config));
    buildFSTModel();
  }

  public ArrayList<String[]> getTemplates()
  {
    return TEMPLATES;
  }

  private static ArrayList<String[]> createTempltes()
  {
    ArrayList list = new ArrayList(1);

    String jsBody = "/**\r\n * TODO description\r\n */\r\n function educationObject_#classname# () {\n\n}";

    String[] jsTemplate = { 
      "Javascript", 
      "js", 
      jsBody };

    list.add(jsTemplate);

    String htmlBody = "<!-- TODO description -->\r\n<!DOCTYPE html>\r\n<html>\r\n<body>\r\n</body>\r\n</html>\r\n";

    String[] htmlTemplate = { 
      "HyperText Markup Language (HTML)", 
      "html", 
      htmlBody };

    list.add(htmlTemplate);
    return list;
  }

  public String replaceMarker(String text, List<String> list, String packageName)
  {
    if ((list != null) && (list.contains("refines")))
      text = text.replace("#refines#", "refines");
    else {
      text = text.replace("#refines# ", "");
    }
    return text;
  }

  public boolean refines()
  {
    return Boolean.TRUE.booleanValue();
  }

  public void postCompile(IResourceDelta delta, IFile file)
  {
    super.postCompile(delta, file);
    try {
      file.refreshLocal(0, null);
    } catch (CoreException e) {
      FeatureJsCorePlugin.getDefault().logError(e);
    }
  }

  public String getConfigurationExtension()
  {
    return "config";
  }

  public void buildFSTModel()
  {
    Long initialTime = Long.valueOf(System.currentTimeMillis());
    br.com.reconcavo.featurejs.metrics.FeatureMetrics.initialTime = initialTime.longValue();
    doBuildFSTModel();
  }

  private void doBuildFSTModel()
  {
    if (!this.tempFolder.exists())
      createTempFolder();
    else {
      try {
        for (IResource res : this.tempFolder.members()) {
          res.delete(Boolean.TRUE.booleanValue(), null);
        }
        this.tempFolder.refreshLocal(2, null);
      } catch (CoreException e) {
        FeatureJsCorePlugin.getDefault().logError(e);
      }
    }

    if ((this.featureProject != null) && (this.featureProject.getProject() != null)) {
      this.featureJsModelBuilder.resetModel();
      StringBuilder stringBuilder = new StringBuilder();
      for (String name : this.featureProject.getFeatureModel().getConcreteFeatureNames()) {
        stringBuilder.append(name);
        stringBuilder.append("\r\n");
      }

      Object source = new ByteArrayInputStream(stringBuilder.toString().getBytes((Charset)Charset.availableCharsets().get("UTF-8")));

      IFile file = this.parentFolder.getFile("." + getConfigurationExtension());
      try {
        if (file.exists())
          file.setContents((InputStream)source, Boolean.FALSE.booleanValue(), Boolean.TRUE.booleanValue(), null);
        else
          file.create((InputStream)source, Boolean.TRUE.booleanValue(), null);
      }
      catch (CoreException e) {
        FeatureJsCorePlugin.getDefault().logError(e);
      }

      compose(file);
      try {
        this.tempFolder.refreshLocal(2, null);
      } catch (CoreException e) {
        FeatureJsCorePlugin.getDefault().logError(e);
      }
      this.featureJsModelBuilder.buildModel();
    }
  }

  private void compose(IFile config) {
    File fileTemp = new File(this.featureProject.getSourcePath() + File.separator + config.getName());
    try {
      fileTemp.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void copyNotComposedFiles(Configuration c, IFolder destination)
  {
    Map fileFeatures = new HashMap();
    Set<Feature> selectedFeatures = c.getSelectedFeatures();
    Set featuresToMaintain = new HashSet();
    List unselectedFeatures = c.getFeatures();
    String featureName;
    if (selectedFeatures != null) {
      for (Feature feature : selectedFeatures) {
        featureName = feature.getName();

        SelectableFeature selectableFeature = c.getSelectablefeature(featureName);
        unselectedFeatures.remove(selectableFeature);

        featuresToMaintain.add(featureName);
        IFolder folder = this.featureProject.getSourceFolder().getFolder(featureName);
        try
        {
          Map fileFeaturesToAdd = copy(folder, destination);
          CollectionUtil.mergeMap(fileFeatures, fileFeaturesToAdd);
        } catch (Exception e) {
          CorePlugin.getDefault().logError(e);
        }
      }
    }

    Collection<FileFeature> allFileFeatures = fileFeatures.values();

    for (FileFeature fileFeature : allFileFeatures) {
      String configName = selectedFeatures.toString();
      this.fileManager = FileManager.configure(configName, fileFeature, unselectedFeatures, featuresToMaintain);
      try {
        this.fileManager.manageFile();
      } catch (FeatureTreatmentException e) {
        e.printStackTrace();
      }
    }

    FileManager.printMetricsInConsole();
  }

  private Map<String, FileFeature> copy(IFolder featureFolder, IFolder buildFolder)
    throws CoreException, FeatureTreatmentException
  {
    Map fileFeatures = new HashMap();
    if (featureFolder.exists()) {
      for (IResource res : featureFolder.members()) {
        if ((res instanceof IFolder))
          doCopyFolder(buildFolder, fileFeatures, res);
        else if ((res instanceof IFile)) {
          doCopyFile(buildFolder, fileFeatures, res);
        }
      }
    }
    return fileFeatures;
  }

  private void doCopyFolder(IFolder buildFolder, Map<String, FileFeature> fileFeatures, IResource res)
    throws CoreException, FeatureTreatmentException
  {
    IFolder folder = buildFolder.getFolder(res.getName());
    if (!folder.exists()) {
      folder.create(Boolean.FALSE.booleanValue(), Boolean.TRUE.booleanValue(), null);
    }

    Map fileFeaturesToAdd = copy((IFolder)res, folder);
    CollectionUtil.mergeMap(fileFeatures, fileFeaturesToAdd);
  }

  private void doCopyFile(IFolder buildFolder, Map<String, FileFeature> fileFeatures, IResource res)
    throws CoreException
  {
    String fileExt = res.getFileExtension();
    LinkedHashSet extentions = extensions();

    IFile file = buildFolder.getFile(res.getName());
    if (!file.exists()) {
      IPath fullPath = file.getFullPath();
      res.copy(fullPath, Boolean.TRUE.booleanValue(), null);
    }

    if (extentions.contains(fileExt)) {
      URI fileURI = file.getLocationURI();
      IPath ipath = res.getFullPath();
      String[] segmets = ipath.segments();

      String segment = null;
      for (int i = 0; i < segmets.length; i++) {
        segment = segmets[i];
        if (segment.equalsIgnoreCase("features")) {
          segment = segmets[(i + 1)];
          break;
        }
      }

      FileFeature fileFeature = (FileFeature)fileFeatures.get(segment);
      if (fileFeature != null) {
        fileFeature.addFile(fileURI);
      } else {
        fileFeature = new FileFeature();
        fileFeature.addFile(fileURI);
        fileFeature.setAssociateFeature(segment);
        fileFeatures.put(segment, fileFeature);
      }
    }
  }

  public LinkedHashSet<String> extensions()
  {
    LinkedHashSet extensions = new LinkedHashSet(2);
    extensions.add("js");
    extensions.add("html");

    return extensions;
  }

}