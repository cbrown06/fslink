package io.github.cbrown06.fslink;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;



/**
 * Implements {@code fslink} functionality.
 *
 * @author <a href="http://cbrown06.github.io/fslink/">Christopher Brown</a>
 * @since 1.0.0
 */
public final class Fslink
{
	private final Path _link;
	private final Path _resource;


	public Fslink(final Path link, final Path resource)
	{
		_link = Objects.requireNonNull(link, "link");
		_resource = resource;
	}


	public boolean create(final boolean overwrite) throws IOException
	{
		// check if resource exists; if it does, check that it exists (following links)
		if (_resource == null || !Files.exists(_resource))
		{
			throw new FileNotFoundException(Objects.toString(_resource));
		}

		// don't try to clobber the file, unless permitted
		if (Files.exists(_link, LinkOption.NOFOLLOW_LINKS))
		{
			if (!overwrite)
			{
				return false;
			}
			Files.delete(_link);
		}

		Files.createSymbolicLink(_link, _resource);
		return true;
	}


	public boolean delete() throws IOException
	{
		if (Files.isSymbolicLink(_link))
		{
			Files.delete(_link);
			return true;
		}
		return false;
	}
}