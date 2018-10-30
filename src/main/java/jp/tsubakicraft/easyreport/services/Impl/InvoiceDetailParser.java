package jp.tsubakicraft.easyreport.services.Impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jp.tsubakicraft.easyreport.domain.dto.AccountingCustomerPartyDTO;
import jp.tsubakicraft.easyreport.domain.dto.AccountingSupplierPartyDTO;
import jp.tsubakicraft.easyreport.domain.dto.ContactDTO;
import jp.tsubakicraft.easyreport.domain.dto.InvoiceDetailDTO;
import jp.tsubakicraft.easyreport.domain.dto.InvoiceLineDTO;
import jp.tsubakicraft.easyreport.domain.dto.LegalMonetaryTotalDTO;
import jp.tsubakicraft.easyreport.domain.dto.PersonDTO;
import jp.tsubakicraft.easyreport.domain.dto.PostalAddressDTO;
import jp.tsubakicraft.easyreport.domain.dto.TaxTotalDTO;
import jp.tsubakicraft.easyreport.util.XMLUtil;

public class InvoiceDetailParser extends UBLParser {

	public static InvoiceDetailParser sharedInstance = new InvoiceDetailParser();
	
	protected InvoiceDetailParser() {	
	}
	
	public InvoiceDetailDTO parse(Document document) {
		InvoiceDetailDTO invoice = new InvoiceDetailDTO();
		
		invoice.setInvoiceId(objectValue(XMLUtil.getChildElementFromDocument(document, "cbc:ID"), String.class));
		invoice.setIssueDate(objectValue(XMLUtil.getElementFromDocument(document, "cbc:IssueDate"), String.class));
		invoice.setDocumentCurrencyCode(objectValue(XMLUtil.getElementFromDocument(document, "cbc:DocumentCurrencyCode"), String.class));
		
		Element orderReferenceElem = XMLUtil.getElementFromDocument(document, "cac:OrderReference");
		if(orderReferenceElem != null) {
			invoice.setOrderId(objectValue(XMLUtil.getElementFromElement(orderReferenceElem, "cdc:ID"), String.class));
		}
		
		invoice.setAccountingSupplierParty(parseAccountingSupplierParty(document));
		invoice.setAccountingCustomerParty(parseAccountingCustomerParty(document));
		invoice.setTaxTotal(parseTaxTotal(document));
		invoice.setLegalMonetaryTotal(parseLegalMonetaryTotal(document));
		
		NodeList list = document.getElementsByTagName("cac:InvoiceLine");
		List<InvoiceLineDTO> invoiceLines = new ArrayList<InvoiceLineDTO>();
		if(list.getLength() > 0) {
			for(int i = 0; i < list.getLength(); i++) {
				Element element = (Element)list.item(i);
				InvoiceLineDTO invoiceLine = parseInvoiceLine(element);
				if(invoiceLine != null) {
					invoiceLines.add(invoiceLine);
				}
			}
		}
		invoice.setInvoiceLines(invoiceLines);

		return invoice;
	}

	private AccountingSupplierPartyDTO parseAccountingSupplierParty(Document document) {
		Element accountingSupplierPartyElem = XMLUtil.getElementFromDocument(document, "cac:AccountingSupplierParty");
		if(accountingSupplierPartyElem != null) {
			Element partyElem = XMLUtil.getElementFromElement(accountingSupplierPartyElem, "cac:Party");
			if(partyElem != null) {
				AccountingSupplierPartyDTO accountingSupplierParty = new AccountingSupplierPartyDTO();
				Element partyIdentificationElem = XMLUtil.getElementFromElement(partyElem, "cac:PartyIdentification");
				accountingSupplierParty.setId(objectValue(XMLUtil.getElementFromElement(partyIdentificationElem, "cbc:ID"), String.class));
				Element partyNameElem = XMLUtil.getElementFromElement(partyElem, "cac:PartyName");
				accountingSupplierParty.setName(objectValue(XMLUtil.getElementFromElement(partyNameElem, "cbc:Name"), String.class));
				accountingSupplierParty.setPostalAddress(parsePostalAddress(partyElem));
				accountingSupplierParty.setContact(parseContact(partyElem));
				accountingSupplierParty.setPerson(parsePerson(partyElem));
				return accountingSupplierParty;
			}
		}
		return null;
	}
	
	private AccountingCustomerPartyDTO parseAccountingCustomerParty(Document document) {
		Element accountingCustomerPartyElem = XMLUtil.getElementFromDocument(document, "cac:AccountingCustomerParty");
		if(accountingCustomerPartyElem != null) {
			Element partyElem = XMLUtil.getElementFromElement(accountingCustomerPartyElem, "cac:Party");
			if(partyElem != null) {
				AccountingCustomerPartyDTO accountingCustomerParty = new AccountingCustomerPartyDTO();
				Element partyIdentificationElem = XMLUtil.getElementFromElement(partyElem, "cac:PartyIdentification");
				accountingCustomerParty.setId(objectValue(XMLUtil.getElementFromElement(partyIdentificationElem, "cbc:ID"), String.class));
				Element partyNameElem = XMLUtil.getElementFromElement(partyElem, "cac:PartyName");
				accountingCustomerParty.setName(objectValue(XMLUtil.getElementFromElement(partyNameElem, "cbc:Name"), String.class));
				accountingCustomerParty.setPostalAddress(parsePostalAddress(partyElem));
				accountingCustomerParty.setContact(parseContact(partyElem));
				return accountingCustomerParty;
			}
		}
		return null;
	}
	
	private TaxTotalDTO parseTaxTotal(Document document) {
		Element taxTotalElem = XMLUtil.getElementFromDocument(document, "cac:TaxTotal");
		return parseTaxTotal(taxTotalElem);
	}
	
	private TaxTotalDTO parseTaxTotal(Element element) {
		if(element != null) {
			TaxTotalDTO taxTotal = new TaxTotalDTO();
			Element taxAmountElem = XMLUtil.getElementFromElement(element, "cbc:TaxAmount");
			if(taxAmountElem != null) {
				taxTotal.setCurrency(taxAmountElem.getAttribute("currencyID"));
				taxTotal.setTaxAmount(objectValue(taxAmountElem, Float.class));
			}
			Element taxSubtotalElem = XMLUtil.getElementFromElement(element, "cac:TaxSubtotal");
			if(taxSubtotalElem != null) {
				Element taxableAmountElem = XMLUtil.getElementFromElement(taxSubtotalElem, "cbc:TaxableAmount");
				if(taxableAmountElem != null) {
					taxTotal.setTaxableAmount(objectValue(taxableAmountElem, Float.class));
				}
				Element taxCategoryElem = XMLUtil.getElementFromElement(taxSubtotalElem, "cac:TaxCategory");
				if(taxCategoryElem != null) {
					taxTotal.setTaxCategoryId(objectValue(XMLUtil.getElementFromElement(taxCategoryElem, "cbc:ID"), String.class));
					taxTotal.setPercent(objectValue(XMLUtil.getElementFromElement(taxCategoryElem, "cbc:Percent"), Float.class));
					Element taxSchemeElem = XMLUtil.getElementFromElement(taxCategoryElem, "cac:TaxScheme");
					if(taxSchemeElem != null) {
						taxTotal.setTaxSchemeId(objectValue(XMLUtil.getElementFromElement(taxSchemeElem, "cbc:ID"), String.class));
						taxTotal.setTaxSchemeName(objectValue(XMLUtil.getElementFromElement(taxSchemeElem, "cbc:Name"), String.class));
					}
				}
			}
			return taxTotal;
		}
		return null;
	}
	
	private LegalMonetaryTotalDTO parseLegalMonetaryTotal(Document document) {
		Element legalMonetaryTotalElem = XMLUtil.getElementFromDocument(document, "cac:LegalMonetaryTotal");
		if(legalMonetaryTotalElem != null) {
			LegalMonetaryTotalDTO legalMonetaryTotal = new LegalMonetaryTotalDTO();
			legalMonetaryTotal.setLineExtensionAmount(objectValue(XMLUtil.getElementFromElement(legalMonetaryTotalElem, "cbc:LineExtensionAmount"), Float.class));
			legalMonetaryTotal.setTaxExclusiveAmount(objectValue(XMLUtil.getElementFromElement(legalMonetaryTotalElem, "cbc:TaxExclusiveAmount"), Float.class));
			legalMonetaryTotal.setTaxInclusiveAmount(objectValue(XMLUtil.getElementFromElement(legalMonetaryTotalElem, "cbc:TaxInclusiveAmount"), Float.class));
			legalMonetaryTotal.setPayableAmount(objectValue(XMLUtil.getElementFromElement(legalMonetaryTotalElem, "cbc:PayableAmount"), Float.class));
			return legalMonetaryTotal;
		}
		return null;
	}
	
	private InvoiceLineDTO parseInvoiceLine(Element element) {
		InvoiceLineDTO invoiceLine = new InvoiceLineDTO();
		invoiceLine.setId(objectValue(XMLUtil.getElementFromElement(element, "cbc:ID"), String.class));
		Element invoicedQuantityElem = XMLUtil.getElementFromElement(element, "cbc:InvoicedQuantity");
		if(invoicedQuantityElem != null) {
			invoiceLine.setCurrency(invoicedQuantityElem.getAttribute("currencyID"));
			invoiceLine.setInvoicedQuantity(objectValue(invoicedQuantityElem, Integer.class));
		}
		invoiceLine.setTaxTotal(parseTaxTotal(XMLUtil.getElementFromElement(element, "cac:TaxTotal")));
		Element itemElem = XMLUtil.getElementFromElement(element, "cac:Item");
		if(itemElem != null) {
			invoiceLine.setItemDescription(objectValue(XMLUtil.getElementFromElement(itemElem, "cbc:Description"), String.class));
			invoiceLine.setItemName(objectValue(XMLUtil.getElementFromElement(itemElem, "cbc:Name"), String.class));
		}
		Element priceElem = XMLUtil.getElementFromElement(element, "cac:Price");
		if(priceElem != null) {
			invoiceLine.setPrice(objectValue(XMLUtil.getElementFromElement(priceElem, "cbc:PriceAmount"), Float.class));
		}
		return invoiceLine;
	}
	
	private PostalAddressDTO parsePostalAddress(Element deliveryElement) {
		Element paElem = XMLUtil.getElementFromElement(deliveryElement, "cac:PostalAddress");
		if(paElem == null) {
			return null;
		}
		PostalAddressDTO postalAddress = new PostalAddressDTO();
		postalAddress.setStreetName(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:StreetName"), String.class));
		postalAddress.setAdditionalStreetName(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:AdditionalStreetName"), String.class));
		postalAddress.setBuildingNumber(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:BuildingNumber"), String.class));
		postalAddress.setCityName(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:CityName"), String.class));
		postalAddress.setPostalZone(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:PostalZone"), String.class));
		postalAddress.setCountry(parseCountry(paElem));
		return postalAddress;
	}
	
	private String parseCountry(Element deliveryElement) {
		Element elem = XMLUtil.getElementFromElement(deliveryElement, "cac:Country");
		if(elem == null) {
			return null;
		}
		return objectValue(XMLUtil.getElementFromElement(elem, "cbc:IdentificationCode"), String.class);
	}
	
	private ContactDTO parseContact(Element deliveryElement) {
		Element elem = XMLUtil.getElementFromElement(deliveryElement, "cac:Contact");
		if(elem == null) {
			return null;
		}
		ContactDTO contact = new ContactDTO();
		contact.setId(objectValue(XMLUtil.getElementFromElement(elem, "cbc:ID"), String.class));
		contact.setName(objectValue(XMLUtil.getElementFromElement(elem, "cbc:Name"), String.class));
		return contact;
	}
	
	private PersonDTO parsePerson(Element deliveryElement) {
		Element elem = XMLUtil.getElementFromElement(deliveryElement, "cac:Person");
		if(elem == null) {
			return null;
		}
		PersonDTO person = new PersonDTO();
		person.setFirstName(objectValue(XMLUtil.getElementFromElement(elem, "cbc:FirstName"), String.class));
		person.setFamilyName(objectValue(XMLUtil.getElementFromElement(elem, "cbc:FamilyName"), String.class));
		return person;
	}
	
	
}
