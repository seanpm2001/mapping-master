package org.mm.ui.model;

import org.mm.core.MappingExpressionsPersistenceLayer;
import org.mm.renderer.ReferenceRendererOptionsManager;
import org.mm.renderer.Renderer;
import org.mm.ss.SpreadSheetDataSource;
import org.mm.ui.action.SaveMappingsAction;
import org.mm.ui.view.MMApplicationView;
import org.mm.ui.view.MappingsControlView;

public class MMApplicationModel implements MMModel
{
  private final DataSourceModel dataSourceModel;
  private final MappingsExpressionsModel expressionMappingsModel;
  private final Renderer renderer;
  private final ReferenceRendererOptionsManager optionsManager;
  private final MappingExpressionsPersistenceLayer mappingExpressionsPersistenceLayer;

  // TODO Use Optional to get rid of null.
  private MMApplicationView applicationView;
  private String mappingFileName;
  private SaveMappingsAction saveMappingsAction;

  public MMApplicationModel(SpreadSheetDataSource dataSource, Renderer renderer,
    MappingExpressionsPersistenceLayer mappingExpressionsPersistenceLayer)
  {
    this.dataSourceModel = new DataSourceModel(dataSource);
    this.expressionMappingsModel = new MappingsExpressionsModel();
    this.mappingExpressionsPersistenceLayer = mappingExpressionsPersistenceLayer;
    this.renderer = renderer;
    this.optionsManager = new ReferenceRendererOptionsManager(renderer.getReferenceRendererConfiguration());
  }

  public DataSourceModel getDataSourceModel() { return this.dataSourceModel; }

  public void setApplicationView(MMApplicationView view) { this.applicationView = view; }

  public MMApplicationView getApplicationView() { return this.applicationView; }

  public MappingsExpressionsModel getMappingExpressionsModel() { return this.expressionMappingsModel; }

  public Renderer getCoreRenderer() { return this.renderer; }

  public ReferenceRendererOptionsManager getMappingConfigurationOptionsManager() { return this.optionsManager; }

  public MappingExpressionsPersistenceLayer getMappingExpressionsPersistenceLayer()
  {
    return this.mappingExpressionsPersistenceLayer;
  }

  public void setSaveMappingsAction(SaveMappingsAction saveMappingsAction)
  {
    this.saveMappingsAction = saveMappingsAction;
  }

  public void saveMappings()
  {
    if (this.saveMappingsAction != null)
      this.saveMappingsAction.saveMappings();
  }

  public void setMappingFileName(String mappingFileName)
  {
    this.mappingFileName = mappingFileName;
    updateView();
  }

  public void clearMappingFileName()
  {
    this.mappingFileName = null;
    getMappingsControlView().update();
  }

  public void dataSourceUpdated()
  {
    this.renderer.changeDataSource(getDataSourceModel().getDataSource());
  }

  public boolean hasMappingFile() { return this.mappingFileName != null; }

  public String getMappingFileName() { return this.mappingFileName; }

  public boolean areMappingsModified() { return this.expressionMappingsModel.hasBeenModified(); }

  public void clearModifiedStatus() { this.expressionMappingsModel.clearModifiedStatus(); }

  private void updateView()
  {
    if (this.applicationView != null)
      this.applicationView.update();
  }

  private MappingsControlView getMappingsControlView()
  {
    return this.applicationView.getMappingsControlView();
  }
}
