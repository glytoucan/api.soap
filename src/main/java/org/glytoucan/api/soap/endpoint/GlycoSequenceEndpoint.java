package org.glytoucan.api.soap.endpoint;

import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.glycan.DerivatizedMass;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.ResourceEntry;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.glycoinfo.rdf.service.GlycanProcedure;
import org.glytoucan.api.soap.GlycoSequenceDetailRequest;
import org.glytoucan.api.soap.GlycoSequenceDetailResponse;
import org.glytoucan.api.soap.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * 
 * @author aoki
 * 
 *         The specification for this can be found at
 *         http://code.glytoucan.org/system/api_list/.
 *
 *         This work is licensed under the Creative Commons Attribution 4.0
 *         International License. To view a copy of this license, visit
 *         http://creativecommons.org/licenses/by/4.0/.
 *
 */
@Endpoint
public class GlycoSequenceEndpoint {
	private static final Log logger = LogFactory.getLog(GlycoSequenceEndpoint.class);
	private static final String NAMESPACE_URI = "http://soap.api.glytoucan.org/";

	private GlycanProcedure glycanProcedure;

	@Autowired
	public GlycoSequenceEndpoint(GlycanProcedure glycanProcedure) {
		this.glycanProcedure = glycanProcedure;
	}

	/**
	 * 
	 * Query entry using accession number.
	 * 
	 * @param accessionNumber
	 * @return
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "glycoSequenceDetailRequest")
	@ResponsePayload
	public GlycoSequenceDetailResponse queryEntry(@RequestPayload GlycoSequenceDetailRequest request) {
		Assert.notNull(request);
		Assert.notNull(request.getAccessionNumber());

		SparqlEntity se = glycanProcedure.getDescription(request.getAccessionNumber());

		ResponseMessage rm = new ResponseMessage();
		rm.setMessage(se.getValue(GlycanProcedure.Description));
		rm.setErrorCode(new BigInteger("0"));

		GlycoSequenceDetailResponse gsdr = new GlycoSequenceDetailResponse();

		gsdr.setAccessionNumber(se.getValue(ResourceEntry.Identifier));
		gsdr.setDescription(se.getValue(GlycanProcedure.Description));
		gsdr.setIupac(se.getValue(GlycoSequence.Sequence));
		gsdr.setMass(se.getValue(DerivatizedMass.MassLabel));
		gsdr.setSequence(se.getValue(GlycoSequence.Sequence));
		gsdr.setResponseMessage(rm);
		return gsdr;
	}
}