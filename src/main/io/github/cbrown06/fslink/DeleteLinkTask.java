package io.github.cbrown06.fslink;

import java.io.File;



/**
 * {@code createlink} task for Apache Ant.
 *
 * @author <a href="http://cbrown06.github.io/fslink/">Christopher Brown</a>
 * @since 1.0.0
 */
public final class DeleteLinkTask extends FslinkTask
{
	/**
	 * Used by Apache Ant to constructs a new instance of this task.
	 */
	public DeleteLinkTask()
	{
		super(ACTION_DELETE);
	}
}