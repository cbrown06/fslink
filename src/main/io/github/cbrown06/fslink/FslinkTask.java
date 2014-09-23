package io.github.cbrown06.fslink;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import static java.lang.String.format;



/**
 * Abstract filesystem link task for Apache Ant.
 *
 * @author <a href="http://cbrown06.github.io/fslink/">Christopher Brown</a>
 * @since 1.0.0
 */
public abstract class FslinkTask extends Task
{
	/**
	 * Constant for the {@code create} action.
	 */
	protected static final String ACTION_CREATE = "create";

	/**
	 * Constant for the {@code delete} action.
	 */
	protected static final String ACTION_DELETE = "delete";


	private String _action;
	private File _link;
	protected File _resource;
	protected boolean _overwrite;
	private boolean _failonerror;


	protected FslinkTask(final String action)
	{
		switch (Objects.requireNonNull(action, "action"))
		{
			case ACTION_CREATE:
			case ACTION_DELETE:
				break;
			default:
				throw new IllegalArgumentException(format(
					"The \"action\" must be either \"%s\" or \"%s\", was: %s",
					ACTION_CREATE, ACTION_DELETE, action
				));
		}
		_action = action;

		_link = null;
		_resource = null;
		_overwrite = false;
		_failonerror = true;
	}


	@Override
	public void execute() throws BuildException
	{
		final Project project = getProject();

		final Path link = _link != null ? _link.toPath() : null;
		final Path resource = _resource != null ? _resource.toPath() : null;

		if (link == null)
		{
			throw new BuildException(format(
				"The \"link\" attribute must be defined (for \"%s\" action).",
				_action
			), getLocation()
			);
		}

		if (resource == null && !ACTION_DELETE.equals(_action))
		{
			throw new BuildException(format(
				"The \"resource\" attribute must be defined (for \"%s\" action).",
				_action
			), getLocation()
			);
		}

		switch (_action)
		{
			case ACTION_CREATE:
			{
				if (Files.exists(link, LinkOption.NOFOLLOW_LINKS))
				{
					if (!_overwrite)
					{
						final String msg = "Cannot overwrite " + relativize(_link);
						if (_failonerror)
						{
							throw new BuildException(msg, getLocation());
						}
						project.log(this, msg, Project.MSG_WARN);
						return;
					}
				}

				if (resource == null || !Files.exists(resource))
				{
					final String msg = "Cannot create symbolic link, no such resource: " + relativize(_resource);
					if (_failonerror)
					{
						throw new BuildException(msg, getLocation());
					}
					project.log(this, msg, Project.MSG_WARN);
					return;
				}

				try
				{
					if (new Fslink(link, resource).create(_overwrite))
					{
						project.log(this, "Created symbolic link: " + relativize(_link), Project.MSG_INFO);
					}
				}
				catch (IOException e)
				{
					final String msg = format(
						"Failed to create symbolic link (\"%s\", referring to \"%s\"), due to %s",
						relativize(_link), relativize(_resource), e
					);
					if (_failonerror)
					{
						throw new BuildException(msg, e, getLocation());
					}
					project.log(this, msg, Project.MSG_WARN);
					return;
				}

				break;
			}
			case ACTION_DELETE:
			{
				if (!Files.exists(link, LinkOption.NOFOLLOW_LINKS))
				{
					project.log(this, "No symbolic link to delete: " + relativize(_link), Project.MSG_DEBUG);
					return;
				}
				if (!Files.isSymbolicLink(link))
				{
					final String msg = "Cannot delete, not a symbolic link: " + relativize(_link);
					if (_failonerror)
					{
						throw new BuildException(msg, getLocation());
					}
					project.log(this, msg, Project.MSG_WARN);
					return;
				}

				try
				{
					if (new Fslink(link, resource).delete())
					{
						project.log(this, "Deleted symbolic link: " + relativize(_link), Project.MSG_INFO);
					}
				}
				catch (IOException e)
				{
					final String msg = format(
						"Failed to delete symbolic link (\"%s\"), due to %s",
						relativize(_link), e
					);
					if (_failonerror)
					{
						throw new BuildException(msg, e, getLocation());
					}
					project.log(this, msg, Project.MSG_WARN);
					return;
				}

				break;
			}
		}
	}


	private String relativize(final File f)
	{
		String pp = getProject().getBaseDir().getAbsolutePath();
		String fp = f.getAbsolutePath();
		if (fp.length() > pp.length() && fp.startsWith(pp))
		{
			fp = fp.substring(pp.length());
			if (fp.length() > 1)
			{
				final char c0 = fp.charAt(0);
				if (c0 == File.separatorChar || c0 == '/')
				{
					fp = fp.substring(1);
				}
			}
		}
		return fp;
	}


	/**
	 * Sets the {@code link} property.
	 * This is mandatory.
	 *
	 * @param link the property value, as specified in the build file.
	 */
	public final void setLink(File link)
	{
		_link = link;
	}


	/**
	 * Sets the {@code failonerror} property.
	 * This is optional, defaulting to {@code true}.
	 *
	 * @param failonerror the property value, as specified in the build file.
	 */
	public final void setFailonerror(boolean failonerror)
	{
		_failonerror = failonerror;
	}
}