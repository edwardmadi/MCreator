/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.minecraft;

import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.text.WordUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataListEntry implements Comparable<DataListEntry> {

	private final String name;
	private String readableName;
	private String description;
	private String type;
	private String texture;
	private Object other;

	@Nullable public List<String> required_apis;

	private final Set<GeneratorConfiguration> supportedGenerators;

	DataListEntry(String name) {
		this.name = name;
		this.supportedGenerators = new HashSet<>();
	}

	public void setReadableName(String readableName) {
		this.readableName = readableName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setOther(Object other) {
		this.other = other;
	}

	public String getName() {
		return name;
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public String getReadableName() {
		if (readableName == null) {
			if (name.startsWith(NameMapper.MCREATOR_PREFIX)) {
				return WordUtils.capitalizeFully(
						StringUtils.machineToReadableName(name.replace(NameMapper.MCREATOR_PREFIX, "")));
			} else if (name.startsWith("TAG:")) {
				return name;
			} else {
				return WordUtils.capitalizeFully(StringUtils.machineToReadableName(name));
			}
		}
		return readableName;
	}

	public String getDescription() {
		if (description == null)
			return "";
		return description;
	}

	public List<String> getRequiredAPIs() {
		return required_apis;
	}

	public void setRequiredAPIs(List<String> required_apis) {
		this.required_apis = required_apis;
	}

	public String getType() {
		return type;
	}

	public Object getOther() {
		return other;
	}

	@Override public String toString() {
		return name;
	}

	@Override public boolean equals(Object o) {
		return o instanceof DataListEntry && name.equals(((DataListEntry) o).name);
	}

	@Override public int hashCode() {
		return name.hashCode();
	}

	@Override public int compareTo(DataListEntry o) {
		String a = this.getReadableName();
		String b = o.getReadableName();

		String a_ = this.getName();
		String b_ = o.getName();

		if (a_.startsWith(NameMapper.MCREATOR_PREFIX) && !b_.startsWith(NameMapper.MCREATOR_PREFIX))
			return -1;
		else if (!a_.startsWith(NameMapper.MCREATOR_PREFIX) && b_.startsWith(NameMapper.MCREATOR_PREFIX))
			return 1;
		return a.compareToIgnoreCase(b);
	}

	public void addSupportedGenerator(GeneratorConfiguration generatorConfiguration) {
		supportedGenerators.add(generatorConfiguration);
	}

	public boolean isSupportedInWorkspace(Workspace workspace) {
		if (required_apis != null) {
			for (String required_api : required_apis) {
				if (!workspace.getWorkspaceSettings().getMCreatorDependencies().contains(required_api)) {
					return false;
				}
			}
		}

		return supportedGenerators.contains(workspace.getGeneratorConfiguration());
	}

	public static class Null extends DataListEntry {

		public Null() {
			super("");
		}

		@Override public boolean isSupportedInWorkspace(Workspace workspace) {
			return true;
		}

	}

	public static class Dummy extends DataListEntry {

		public Dummy(String name) {
			super(name);
		}

		@Override public boolean isSupportedInWorkspace(Workspace workspace) {
			return true;
		}

	}

	public static class Custom extends DataListEntry {

		private final ModElement modElement;

		public Custom(ModElement modElement) {
			this(modElement, null);
		}

		public Custom(ModElement modElement, @Nullable String suffix) {
			super(NameMapper.MCREATOR_PREFIX + modElement.getName() + (suffix != null ? suffix : ""));

			this.modElement = modElement;

			setDescription(modElement.getType().getDescription());
		}

		public ModElement getModElement() {
			return modElement;
		}

		@Override public boolean isSupportedInWorkspace(Workspace workspace) {
			return true;
		}

	}

}
