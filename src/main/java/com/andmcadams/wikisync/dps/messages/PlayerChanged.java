package com.andmcadams.wikisync.dps.messages;

import lombok.Value;

@Value
public class PlayerChanged
{

	String _wsType = "PlayerChanged";
	String name;

}
