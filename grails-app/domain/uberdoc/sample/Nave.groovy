package uberdoc.sample

import sample.Planet

class Nave {

    String name
    String shipData
    Planet planet

    static hasOne = [captain: Pessoa]
}
