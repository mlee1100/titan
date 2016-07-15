
g = TitanFactory.open('conf/titan-berkeleydb-es.properties')
mgmt = g.getManagementSystem()
mgmt.buildIndex('ByEmail',Vertex.class).addKey(mgmt.makePropertyKey('email').dataType(String.class).make()).unique().buildCompositeIndex()
mgmt.buildIndex('ByPhone',Vertex.class).addKey(mgmt.makePropertyKey('phone').dataType(String.class).make()).unique().buildCompositeIndex()
mgmt.buildIndex('ByDate_Of_Birth',Vertex.class).addKey(mgmt.makePropertyKey('date_of_birth').dataType(String.class).make()).unique().buildCompositeIndex()
mgmt.buildIndex('ByYear_Of_Birth',Vertex.class).addKey(mgmt.makePropertyKey('year_of_birth').dataType(String.class).make()).buildCompositeIndex()
mgmt.buildIndex('ByYear_Gathered',Edge.class).addKey(mgmt.makePropertyKey('year_gathered').dataType(Integer.class).make()).buildCompositeIndex()
mgmt.buildIndex('ByMonth_Gathered',Edge.class).addKey(mgmt.makePropertyKey('month_gathered').dataType(Integer.class).make()).buildCompositeIndex()
mgmt.buildIndex('ByEmployment_Status',Vertex.class).addKey(mgmt.makePropertyKey('employment_status').dataType(String.class).make()).unique().buildCompositeIndex()
mgmt.buildIndex('ByFull_Name',Vertex.class).addKey(mgmt.makePropertyKey('full_name').dataType(String.class).make()).unique().buildCompositeIndex()
mgmt.buildIndex('ByFull_Address',Vertex.class).addKey(mgmt.makePropertyKey('full_address').dataType(String.class).make()).unique().buildCompositeIndex()
mgmt.buildIndex('ByState',Vertex.class).addKey(mgmt.makePropertyKey('state').dataType(String.class).make()).buildCompositeIndex()
mgmt.buildIndex('ByZip',Vertex.class).addKey(mgmt.makePropertyKey('zip').dataType(String.class).make()).buildCompositeIndex()
// mgmt.makeEdgeLabel('aged').multiplicity(MULTI).make()
// mgmt.makeEdgeLabel('employed').multiplicity(MULTI).make()
// mgmt.makeEdgeLabel('lives').multiplicity(MULTI).make()
// mgmt.makeEdgeLabel('contacted').multiplicity(MULTI).make()
// mgmt.makeEdgeLabel('named').multiplicity(MULTI).make()
mgmt.commit()
g.commit()

def nullValue (inVal) {
  if (inVal == '') {
    return null
  } else {
    return inVal
  }
}

static void addProperties (construct,vertex,prop_name,prop_val) {
  if (prop_val != null) {
    if (construct == 'edge') {
      vertex.setProperty(prop_name,prop_val)
    } else if (construct == 'vertex') {
      vertex.addProperty(prop_name,prop_val)
    }
  }
}

static void addDefaultEdgeProperties (edge_label,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded) {
  if (source != null) { edge_label.setProperty('source',source) }
  if (year_gathered != null) { edge_label.setProperty('year_gathered',year_gathered) }
  if (month_gathered != null) { edge_label.setProperty('month_gathered',month_gathered) }
  if (day_gathered != null) { edge_label.setProperty('day_gathered',day_gathered) }
  if (year_uploaded != null) { edge_label.setProperty('year_uploaded',year_uploaded) }
  if (month_uploaded != null) { edge_label.setProperty('month_uploaded',month_uploaded) }
  if (day_uploaded != null) { edge_label.setProperty('day_uploaded',day_uploaded) }
}

committer = 0
batch_size = 500

// g = TitanFactory.open('conf/titan-berkeleydb-es.properties')
new File('data/coreg-sample-full.txt').eachLine { line ->
  def (last_name, email_md5, year_gathered, city, first_name, zip, employment_status, source, date_of_birth, month_gathered, day_of_birth, email, full_address, phone, year_of_birth, address, day_gathered, month_uploaded, gender, age, month_of_birth, income_tax_tier, state_short, day_uploaded, year_uploaded, full_name) = line.split( '\\|' ).collect( { nullValue(it.trim()) })

  if (email != 'email') {

    try {
      year_gathered = year_gathered.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    try {
      month_gathered = month_gathered.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    try {
      day_of_birth = day_of_birth.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    try {
      year_of_birth = year_of_birth.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    try {
      day_gathered = day_gathered.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    try {
      month_uploaded = month_uploaded.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    try {
      age = age.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    try {
      month_of_birth = month_of_birth.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    try {
      day_uploaded = day_uploaded.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    try {
      year_uploaded = year_uploaded.toInteger()
    } catch(Exception e) {
      // do nothing
    }

    // check if value is populated for current record
    // println email
    if (email != '' && email != null) {
      type = 'email'
      //create vertex if it doesn't exist
      if (!g.V(type,email).hasNext()) {
        vEmail = g.addVertex(type:type)
        addProperties('vertex',vEmail,'email',email)
        addProperties('vertex',vEmail,'email_md5',email_md5)
      }
    }
    // println age
    // check if value is populated for current record
    if (date_of_birth != '' && date_of_birth != null) {
      type = 'date_of_birth'
      //create vertex if it doesn't exist
      if (!g.V(type,date_of_birth).hasNext()) {
        vAge = g.addVertex(type:type)
        addProperties('vertex',vAge,'date_of_birth',date_of_birth)
        addProperties('vertex',vAge,'age',age)
        addProperties('vertex',vAge,'year_of_birth',year_of_birth)
        addProperties('vertex',vAge,'month_of_birth',month_of_birth)
        addProperties('vertex',vAge,'day_of_birth',day_of_birth)
      }
      // add edge from email to new vertex
      eEmailAge = vEmail.addEdge('aged',vAge)
      addDefaultEdgeProperties(eEmailAge,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded)
    }
    // println employment_status
    // check if value is populated for current record
    if (employment_status != '' && employment_status != null) {
      type = 'employment_status'
      //create vertex if it doesn't exist
      if (!g.V(type,employment_status).hasNext()) {
        vEmployment = g.addVertex(type:type)
        addProperties('vertex',vEmployment,'employment_status',employment_status)
        addProperties('vertex',vEmployment,'income_tax_tier',income_tax_tier)
      }
      eEmailEmployment = vEmail.addEdge('employed',vEmployment)
      addDefaultEdgeProperties(eEmailEmployment,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded)
    }
    // println 'name'
    // check if value is populated for current record
    if (full_name != '' && full_name != null) {
      type = 'name'
      //create vertex if it doesn't exist
      if (!g.V('full_name',full_name).hasNext()) {
        vName = g.addVertex(type:type)
        addProperties('vertex',vName,'full_name',full_name)
        addProperties('vertex',vName,'first_name',first_name)
        addProperties('vertex',vName,'last_name',last_name)
        addProperties('vertex',vName,'gender',gender)
      }
      eEmailName = vEmail.addEdge('named',vName)
      addDefaultEdgeProperties(eEmailName,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded)
    }
    // println 'address'
    // check if value is populated for current record
    if (full_address != '' && full_address != null) {
      type = 'address'
      //create vertex if it doesn't exist
      if (!g.V('full_address',full_address).hasNext()) {
        vAddress = g.addVertex(type:type)
        // println 'before address props'
        addProperties('vertex',vAddress,'full_address',full_address)
        addProperties('vertex',vAddress,'address',address)
        addProperties('vertex',vAddress,'city',city)
        addProperties('vertex',vAddress,'state',state_short)
        addProperties('vertex',vAddress,'zip',zip)
        // println full_address
      }
      // println 191
      eEmailAddress = vEmail.addEdge('lives',vAddress)
      // println 193
      addProperties('edge',eEmailAddress,'source',source)
      // println eEmailAddress.map
      def outb = [eEmailAddress,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded]
      // println outb
      addDefaultEdgeProperties(eEmailAddress,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded)
      // println 197
      if (full_name != '' && full_name != null) {
        // if record has a name, add edge to connect email to name
        eAddressName = vAddress.addEdge('named',vName)
        addDefaultEdgeProperties(eAddressName,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded)
      }
    }
    // println 'phone'
    // check if value is populated for current record
    if (phone != '' && phone != null) {
      type = 'phone'
      //create vertex if it doesn't exist
      if (!g.V(type,phone).hasNext()) {
        vPhone = g.addVertex(type:type)
        addProperties('vertex',vPhone,'phone',phone)
      }
      eEmailPhone = vEmail.addEdge('contacted',vPhone)
      addDefaultEdgeProperties(eEmailPhone,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded)
      if (full_address != '' && full_address != null) {
        // if record has an address, add edge to connect phone to address
        ePhoneAddress = vPhone.addEdge('lives',vAddress)
        addDefaultEdgeProperties(ePhoneAddress,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded)
      }
      if (full_name != '' && full_name != null) {
        // if record has a name, add edge to connect phone to name
        ePhoneName = vPhone.addEdge('named',vName)
        addDefaultEdgeProperties(ePhoneName,source,year_gathered,month_gathered,day_gathered,year_uploaded,month_uploaded,day_uploaded)
      }
    }
  }
  committer += 1
  if (committer == batch_size) {
    committer = 0
    g.commit()
  }
}
  
g.commit()

