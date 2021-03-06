package jp.tsubakicraft.easyreport.domain.dto;

public class AccountingCustomerPartyDTO {

	private String id;
	private String name;
	private PostalAddressDTO postalAddress;
	private ContactDTO contact;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PostalAddressDTO getPostalAddress() {
		return postalAddress;
	}
	public void setPostalAddress(PostalAddressDTO postalAddress) {
		this.postalAddress = postalAddress;
	}
	public ContactDTO getContact() {
		return contact;
	}
	public void setContact(ContactDTO contact) {
		this.contact = contact;
	}

}
