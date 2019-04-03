package edu.uah.rsesc.agree.ge.ui.properties;

import org.eclipse.jface.viewers.IFilter;
import org.osate.ge.ui.properties.PropertySectionUtil;

import com.rockwellcollins.atc.agree.agree.OutputStatement;

import edu.uah.rsesc.agree.ge.businessObjectHandlers.OutputStatementHandler;

public class EquationsPropertySection extends GenericPropertySection {
	private final OutputStatementHandler handler = new OutputStatementHandler();

	public static class Filter implements IFilter {
		@Override
		public boolean select(final Object toTest) {
			return AgreePropertySectionUtil.asComponentClassifier(toTest) != null;
		}
	}

	@Override
	protected void onAdd() {
		PropertySectionUtil
				.execute(handler.buildCreateOperation(getSelectedBos().bocStream().findFirst().orElse(null)));
	}

	@Override
	protected String getName(final Object element) {
		return handler.getName((OutputStatement) element);
	}

	@Override
	protected Object[] getSpecStatements() {
		return getSpecStatements(AgreePropertySectionUtil::asComponentClassifier, OutputStatement.class);
	}

}
