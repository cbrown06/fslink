package io.github.cbrown06.fslink;

import java.io.File;



/**
 * {@code createlink} task for Apache Ant.
 *
 * @author <a href="http://cbrown06.github.io/fslink/">Christopher Brown</a>
 * @since 1.0.0
 */
public final class CreateLinkTask extends FslinkTask
{
	/**
	 * Used by Apache Ant to constructs a new instance of this task.
	 */
	public CreateLinkTask()
	{
		super(ACTION_CREATE);
	}


	/**
	 * Sets the {@code overwrite} property.
	 * This is optional, defaulting to {@code false}.
	 *
	 * @param overwrite the property value, as specified in the build file.
	 */
	public void setOverwrite(boolean overwrite)
	{
		_overwrite = overwrite;
	}


	/**
	 * Sets the {@code resource} property.
	 * This is mandatory.
	 *
	 * @param resource the property value, as specified in the build file.
	 */
	public void setResource(File resource)
	{
		_resource = resource;
	}
}