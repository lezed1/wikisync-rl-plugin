package com.andmcadams.wikisync.dps.messages;

import lombok.Value;

@Value
public class UsernameChanged
{

	RequestType _wsType = RequestType.UsernameChanged;
	String username;

}
