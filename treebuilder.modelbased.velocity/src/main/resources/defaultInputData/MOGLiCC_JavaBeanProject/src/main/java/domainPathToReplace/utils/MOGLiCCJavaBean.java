package <domainPathToReplace>.utils;

/**
 * Only necessary for the JavaBeanRegistry mechanism
 * which allows reciprocal referencing between JavaBeans.
 * 
 * @author Reik Oberrath
 */
public interface MOGLiCCJavaBean
{
	public String getRegistryId();
	public void setRegistryId(String registryId);
	
}
