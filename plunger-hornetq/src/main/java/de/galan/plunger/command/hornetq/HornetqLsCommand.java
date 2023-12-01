package de.galan.plunger.command.hornetq;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.Arrays;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientRequestor;
import org.hornetq.api.core.client.ClientSession.QueueQuery;
import org.hornetq.api.core.management.ManagementHelper;
import org.hornetq.api.core.management.ResourceNames;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractLsCommand;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.StringCaseInsensitiveComparator;


/**
 * Lists all destinations on a HornetQ messaging server.
 */
public class HornetqLsCommand extends AbstractLsCommand {

	HornetqCore core;
	HornetqUtil util;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		util = new HornetqUtil();
		core = new HornetqCore();
		core.initialize(pa, util.getTransportConfiguration(pa));
	}


	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		try {
			ClientRequestor requestor = new ClientRequestor(core.getSession(), "jms.queue.hornetq.management");
			ClientMessage message = core.getSession().createMessage(false);
			ManagementHelper.putAttribute(message, ResourceNames.CORE_SERVER, "addressNames");
			ClientMessage reply = requestor.request(message);
			Object[] arrayObjects = (Object[])ManagementHelper.getResult(reply);
			//TODO if destination is given, list only single destination
			String[] array = Arrays.copyOf(arrayObjects, arrayObjects.length, String[].class);
			Arrays.sort(array, new StringCaseInsensitiveComparator());
			for (String address: array) {
				String addressPlunger = removeStart(address, "jms.");
				boolean managementQueue = addressPlunger.matches("queue.hornetq.management.[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
				boolean matchesTarget = pa.getTarget().isDestinationErased() || addressPlunger.equals(pa.getTarget().getDestination());
				if (!managementQueue && matchesTarget) {
					QueueQuery queueQuery = core.getSession().queueQuery(SimpleString.toSimpleString(address));
					printDestination(pa, addressPlunger, queueQuery.getConsumerCount(), queueQuery.getMessageCount(), queueQuery.isDurable());
				}
			}
		}
		catch (Exception ex) {
			throw new CommandException("Could not process ls", ex);
		}
	}


	@Override
	protected void close() {
		core.close();
	}

}
