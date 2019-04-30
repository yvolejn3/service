package yvolejn3.service.main.bots

import com.fasterxml.jackson.databind.JsonNode
import com.google.inject.ImplementedBy
import yvolejn3.service.main.bots.impl.DefaultBot

@ImplementedBy(DefaultBot)
interface Bot {

    JsonNode start(String login)

}