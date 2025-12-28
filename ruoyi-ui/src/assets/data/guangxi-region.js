/**
 * 广西壮族自治区行政区划数据
 * 数据来源：基于国家统计局公开数据整理
 * 层级：省 -> 市 -> 县/区 -> 乡镇/街道
 * 更新时间：2024年
 */

var guangxiRegions = [
  {
    value: '450100',
    label: '南宁市',
    children: [
      {
        value: '450102',
        label: '兴宁区',
        children: [
          { value: '450102001', label: '朝阳街道' },
          { value: '450102002', label: '民生街道' },
          { value: '450102003', label: '三塘镇' },
          { value: '450102004', label: '五塘镇' },
          { value: '450102005', label: '昆仑镇' }
        ]
      },
      {
        value: '450103',
        label: '青秀区',
        children: [
          { value: '450103001', label: '中山街道' },
          { value: '450103002', label: '建政街道' },
          { value: '450103003', label: '南湖街道' },
          { value: '450103004', label: '津头街道' },
          { value: '450103005', label: '刘圩镇' },
          { value: '450103006', label: '南阳镇' },
          { value: '450103007', label: '长塘镇' },
          { value: '450103008', label: '伶俐镇' }
        ]
      },
      {
        value: '450105',
        label: '江南区',
        children: [
          { value: '450105001', label: '福建园街道' },
          { value: '450105002', label: '江南街道' },
          { value: '450105003', label: '沙井街道' },
          { value: '450105004', label: '那洪街道' },
          { value: '450105005', label: '吴圩镇' },
          { value: '450105006', label: '苏圩镇' },
          { value: '450105007', label: '延安镇' }
        ]
      },
      {
        value: '450107',
        label: '西乡塘区',
        children: [
          { value: '450107001', label: '西乡塘街道' },
          { value: '450107002', label: '北湖街道' },
          { value: '450107003', label: '衡阳街道' },
          { value: '450107004', label: '安宁街道' },
          { value: '450107005', label: '华强街道' },
          { value: '450107006', label: '新阳街道' },
          { value: '450107007', label: '上尧街道' },
          { value: '450107008', label: '安吉街道' },
          { value: '450107009', label: '心圩街道' },
          { value: '450107010', label: '相思湖街道' },
          { value: '450107011', label: '石埠街道' },
          { value: '450107012', label: '金陵镇' },
          { value: '450107013', label: '双定镇' }
        ]
      },
      {
        value: '450108',
        label: '良庆区',
        children: [
          { value: '450108001', label: '大沙田街道' },
          { value: '450108002', label: '良庆街道' },
          { value: '450108003', label: '玉洞街道' },
          { value: '450108004', label: '那马镇' },
          { value: '450108005', label: '那陈镇' },
          { value: '450108006', label: '大塘镇' },
          { value: '450108007', label: '南晓镇' }
        ]
      },
      {
        value: '450109',
        label: '邕宁区',
        children: [
          { value: '450109001', label: '蒲庙镇' },
          { value: '450109002', label: '新江镇' },
          { value: '450109003', label: '百济镇' },
          { value: '450109004', label: '那楼镇' },
          { value: '450109005', label: '中和镇' }
        ]
      },
      {
        value: '450110',
        label: '武鸣区',
        children: [
          { value: '450110001', label: '城厢镇' },
          { value: '450110002', label: '太平镇' },
          { value: '450110003', label: '双桥镇' },
          { value: '450110004', label: '宁武镇' },
          { value: '450110005', label: '锣圩镇' },
          { value: '450110006', label: '府城镇' },
          { value: '450110007', label: '两江镇' },
          { value: '450110008', label: '罗波镇' },
          { value: '450110009', label: '陆斡镇' },
          { value: '450110010', label: '马头镇' },
          { value: '450110011', label: '仙湖镇' },
          { value: '450110012', label: '甘圩镇' },
          { value: '450110013', label: '灵马镇' }
        ]
      },
      {
        value: '450123',
        label: '隆安县',
        children: [
          { value: '450123100', label: '城厢镇' },
          { value: '450123101', label: '南圩镇' },
          { value: '450123102', label: '雁江镇' },
          { value: '450123103', label: '那桐镇' },
          { value: '450123104', label: '乔建镇' },
          { value: '450123105', label: '丁当镇' },
          { value: '450123200', label: '古潭乡' },
          { value: '450123201', label: '都结乡' },
          { value: '450123202', label: '布泉乡' },
          { value: '450123203', label: '屏山乡' }
        ]
      },
      {
        value: '450124',
        label: '马山县',
        children: [
          { value: '450124100', label: '白山镇' },
          { value: '450124101', label: '百龙滩镇' },
          { value: '450124102', label: '林圩镇' },
          { value: '450124103', label: '古零镇' },
          { value: '450124104', label: '金钗镇' },
          { value: '450124105', label: '周鹿镇' },
          { value: '450124106', label: '永州镇' },
          { value: '450124200', label: '乔利乡' },
          { value: '450124201', label: '加方乡' },
          { value: '450124202', label: '古寨瑶族乡' },
          { value: '450124203', label: '里当瑶族乡' }
        ]
      },
      {
        value: '450125',
        label: '上林县',
        children: [
          { value: '450125100', label: '大丰镇' },
          { value: '450125101', label: '明亮镇' },
          { value: '450125102', label: '巷贤镇' },
          { value: '450125103', label: '白圩镇' },
          { value: '450125104', label: '三里镇' },
          { value: '450125105', label: '乔贤镇' },
          { value: '450125106', label: '西燕镇' },
          { value: '450125200', label: '澄泰乡' },
          { value: '450125201', label: '木山乡' },
          { value: '450125202', label: '塘红乡' },
          { value: '450125203', label: '镇圩瑶族乡' }
        ]
      },
      {
        value: '450126',
        label: '宾阳县',
        children: [
          { value: '450126100', label: '宾州镇' },
          { value: '450126101', label: '黎塘镇' },
          { value: '450126102', label: '甘棠镇' },
          { value: '450126103', label: '思陇镇' },
          { value: '450126104', label: '新桥镇' },
          { value: '450126105', label: '新圩镇' },
          { value: '450126106', label: '邹圩镇' },
          { value: '450126107', label: '大桥镇' },
          { value: '450126108', label: '武陵镇' },
          { value: '450126109', label: '中华镇' },
          { value: '450126110', label: '古辣镇' },
          { value: '450126111', label: '露圩镇' },
          { value: '450126112', label: '王灵镇' },
          { value: '450126113', label: '和吉镇' },
          { value: '450126200', label: '洋桥镇' },
          { value: '450126201', label: '陈平镇' }
        ]
      },
      {
        value: '450181',
        label: '横州市',
        children: [
          { value: '450181100', label: '横州镇' },
          { value: '450181101', label: '百合镇' },
          { value: '450181102', label: '那阳镇' },
          { value: '450181103', label: '南乡镇' },
          { value: '450181104', label: '新福镇' },
          { value: '450181105', label: '莲塘镇' },
          { value: '450181106', label: '平马镇' },
          { value: '450181107', label: '峦城镇' },
          { value: '450181108', label: '六景镇' },
          { value: '450181109', label: '石塘镇' },
          { value: '450181110', label: '陶圩镇' },
          { value: '450181111', label: '校椅镇' },
          { value: '450181112', label: '云表镇' },
          { value: '450181200', label: '马岭镇' },
          { value: '450181201', label: '马山镇' },
          { value: '450181202', label: '平朗乡' },
          { value: '450181203', label: '镇龙乡' }
        ]
      }
    ]
  },
  {
    value: '450200',
    label: '柳州市',
    children: [
      {
        value: '450202',
        label: '城中区',
        children: [
          { value: '450202001', label: '城中街道' },
          { value: '450202002', label: '公园街道' },
          { value: '450202003', label: '潭中街道' },
          { value: '450202004', label: '中南街道' },
          { value: '450202005', label: '河东街道' },
          { value: '450202006', label: '静兰街道' }
        ]
      },
      {
        value: '450203',
        label: '鱼峰区',
        children: [
          { value: '450203001', label: '天马街道' },
          { value: '450203002', label: '驾鹤街道' },
          { value: '450203003', label: '荣军街道' },
          { value: '450203004', label: '箭盘街道' },
          { value: '450203005', label: '五里亭街道' },
          { value: '450203006', label: '白莲街道' },
          { value: '450203007', label: '麒麟街道' },
          { value: '450203008', label: '里雍镇' }
        ]
      },
      {
        value: '450204',
        label: '柳南区',
        children: [
          { value: '450204001', label: '鹅山街道' },
          { value: '450204002', label: '柳南街道' },
          { value: '450204003', label: '河西街道' },
          { value: '450204004', label: '柳石街道' },
          { value: '450204005', label: '银山街道' },
          { value: '450204006', label: '潭西街道' },
          { value: '450204007', label: '太阳村镇' },
          { value: '450204008', label: '洛满镇' },
          { value: '450204009', label: '流山镇' }
        ]
      },
      {
        value: '450205',
        label: '柳北区',
        children: [
          { value: '450205001', label: '解放街道' },
          { value: '450205002', label: '雀儿山街道' },
          { value: '450205003', label: '胜利街道' },
          { value: '450205004', label: '钢城街道' },
          { value: '450205005', label: '跃进街道' },
          { value: '450205006', label: '锦绣街道' },
          { value: '450205007', label: '白露街道' },
          { value: '450205008', label: '石碑坪镇' },
          { value: '450205009', label: '沙塘镇' },
          { value: '450205010', label: '长塘镇' }
        ]
      },
      {
        value: '450206',
        label: '柳江区',
        children: [
          { value: '450206100', label: '拉堡镇' },
          { value: '450206101', label: '进德镇' },
          { value: '450206102', label: '成团镇' },
          { value: '450206103', label: '洛满镇' },
          { value: '450206104', label: '流山镇' },
          { value: '450206105', label: '三都镇' },
          { value: '450206106', label: '里高镇' },
          { value: '450206107', label: '土博镇' },
          { value: '450206108', label: '百朋镇' },
          { value: '450206109', label: '穿山镇' }
        ]
      },
      {
        value: '450222',
        label: '柳城县',
        children: [
          { value: '450222100', label: '大埔镇' },
          { value: '450222101', label: '龙头镇' },
          { value: '450222102', label: '太平镇' },
          { value: '450222103', label: '沙埔镇' },
          { value: '450222104', label: '东泉镇' },
          { value: '450222105', label: '凤山镇' },
          { value: '450222106', label: '六塘镇' },
          { value: '450222107', label: '冲脉镇' },
          { value: '450222200', label: '寨隆镇' },
          { value: '450222201', label: '马山镇' },
          { value: '450222202', label: '古砦仫佬族乡' },
          { value: '450222203', label: '社冲乡' }
        ]
      },
      {
        value: '450223',
        label: '鹿寨县',
        children: [
          { value: '450223100', label: '鹿寨镇' },
          { value: '450223101', label: '中渡镇' },
          { value: '450223102', label: '寨沙镇' },
          { value: '450223103', label: '平山镇' },
          { value: '450223104', label: '黄冕镇' },
          { value: '450223105', label: '四排镇' },
          { value: '450223200', label: '江口乡' },
          { value: '450223201', label: '导江乡' },
          { value: '450223202', label: '拉沟乡' }
        ]
      },
      {
        value: '450224',
        label: '融安县',
        children: [
          { value: '450224100', label: '长安镇' },
          { value: '450224101', label: '浮石镇' },
          { value: '450224102', label: '泗顶镇' },
          { value: '450224103', label: '板榄镇' },
          { value: '450224200', label: '大将镇' },
          { value: '450224201', label: '大良镇' },
          { value: '450224202', label: '雅瑶乡' },
          { value: '450224203', label: '大坡乡' },
          { value: '450224204', label: '沙子乡' },
          { value: '450224205', label: '桥板乡' },
          { value: '450224206', label: '东起乡' },
          { value: '450224207', label: '潭头乡' }
        ]
      },
      {
        value: '450225',
        label: '融水苗族自治县',
        children: [
          { value: '450225100', label: '融水镇' },
          { value: '450225101', label: '和睦镇' },
          { value: '450225102', label: '三防镇' },
          { value: '450225103', label: '怀宝镇' },
          { value: '450225200', label: '洞头镇' },
          { value: '450225201', label: '大浪镇' },
          { value: '450225202', label: '永乐镇' },
          { value: '450225203', label: '四荣乡' },
          { value: '450225204', label: '香粉乡' },
          { value: '450225205', label: '安太乡' },
          { value: '450225206', label: '安陲乡' },
          { value: '450225207', label: '滚贝侗族乡' },
          { value: '450225208', label: '杆洞乡' },
          { value: '450225209', label: '汪洞乡' },
          { value: '450225210', label: '同练瑶族乡' },
          { value: '450225211', label: '红水乡' },
          { value: '450225212', label: '拱洞乡' },
          { value: '450225213', label: '良寨乡' },
          { value: '450225214', label: '大年乡' }
        ]
      },
      {
        value: '450226',
        label: '三江侗族自治县',
        children: [
          { value: '450226100', label: '古宜镇' },
          { value: '450226101', label: '丹洲镇' },
          { value: '450226102', label: '斗江镇' },
          { value: '450226200', label: '独峒镇' },
          { value: '450226201', label: '八江镇' },
          { value: '450226202', label: '林溪镇' },
          { value: '450226203', label: '程村乡' },
          { value: '450226204', label: '老堡乡' },
          { value: '450226205', label: '良口乡' },
          { value: '450226206', label: '洋溪乡' },
          { value: '450226207', label: '同乐苗族乡' },
          { value: '450226208', label: '富禄苗族乡' },
          { value: '450226209', label: '梅林乡' },
          { value: '450226210', label: '和平乡' },
          { value: '450226211', label: '高基瑶族乡' }
        ]
      }
    ]
  },
  {
    value: '450300',
    label: '桂林市',
    children: [
      {
        value: '450302',
        label: '秀峰区',
        children: [
          { value: '450302001', label: '秀峰街道' },
          { value: '450302002', label: '甲山街道' },
          { value: '450302003', label: '丽君街道' }
        ]
      },
      {
        value: '450303',
        label: '叠彩区',
        children: [
          { value: '450303001', label: '叠彩街道' },
          { value: '450303002', label: '北门街道' },
          { value: '450303003', label: '大河乡' }
        ]
      },
      {
        value: '450304',
        label: '象山区',
        children: [
          { value: '450304001', label: '象山街道' },
          { value: '450304002', label: '平山街道' },
          { value: '450304003', label: '南门街道' },
          { value: '450304004', label: '二塘街道' }
        ]
      },
      {
        value: '450305',
        label: '七星区',
        children: [
          { value: '450305001', label: '七星街道' },
          { value: '450305002', label: '东江街道' },
          { value: '450305003', label: '穿山街道' },
          { value: '450305004', label: '漓东街道' },
          { value: '450305005', label: '朝阳乡' }
        ]
      },
      {
        value: '450311',
        label: '雁山区',
        children: [
          { value: '450311001', label: '雁山镇' },
          { value: '450311100', label: '柘木镇' },
          { value: '450311101', label: '草坪回族乡' }
        ]
      },
      {
        value: '450312',
        label: '临桂区',
        children: [
          { value: '450312100', label: '临桂镇' },
          { value: '450312101', label: '六塘镇' },
          { value: '450312102', label: '会仙镇' },
          { value: '450312103', label: '两江镇' },
          { value: '450312104', label: '五通镇' },
          { value: '450312105', label: '南边山镇' },
          { value: '450312106', label: '中庸镇' },
          { value: '450312107', label: '四塘镇' },
          { value: '450312108', label: '茶洞镇' },
          { value: '450312200', label: '宛田瑶族乡' },
          { value: '450312201', label: '黄沙瑶族乡' }
        ]
      },
      { value: '450321', label: '阳朔县', children: [{ value: '450321100', label: '阳朔镇' }, { value: '450321101', label: '白沙镇' }, { value: '450321102', label: '兴坪镇' }, { value: '450321103', label: '福利镇' }, { value: '450321104', label: '葡萄镇' }, { value: '450321105', label: '高田镇' }] },
      { value: '450323', label: '灵川县', children: [{ value: '450323100', label: '灵川镇' }, { value: '450323101', label: '大圩镇' }, { value: '450323102', label: '定江镇' }, { value: '450323103', label: '三街镇' }, { value: '450323104', label: '潭下镇' }, { value: '450323105', label: '灵田镇' }, { value: '450323106', label: '九屋镇' }, { value: '450323107', label: '潮田乡' }, { value: '450323108', label: '海洋乡' }, { value: '450323109', label: '大境瑶族乡' }, { value: '450323110', label: '公平乡' }] },
      { value: '450324', label: '全州县', children: [{ value: '450324100', label: '全州镇' }, { value: '450324101', label: '黄沙河镇' }, { value: '450324102', label: '庙头镇' }, { value: '450324103', label: '文桥镇' }, { value: '450324104', label: '大西江镇' }, { value: '450324105', label: '龙水镇' }, { value: '450324106', label: '才湾镇' }, { value: '450324107', label: '绍水镇' }, { value: '450324108', label: '石塘镇' }, { value: '450324109', label: '西岭镇' }, { value: '450324110', label: '咸水镇' }, { value: '450324111', label: '凤凰镇' }, { value: '450324200', label: '安和镇' }, { value: '450324201', label: '枧塘镇' }, { value: '450324202', label: '两河镇' }, { value: '450324203', label: '永岁镇' }, { value: '450324204', label: '蕉江瑶族乡' }, { value: '450324205', label: '东山瑶族乡' }] },
      { value: '450325', label: '兴安县', children: [{ value: '450325100', label: '兴安镇' }, { value: '450325101', label: '湘漓镇' }, { value: '450325102', label: '界首镇' }, { value: '450325103', label: '高尚镇' }, { value: '450325104', label: '严关镇' }, { value: '450325105', label: '溶江镇' }, { value: '450325106', label: '漠川乡' }, { value: '450325200', label: '崔家乡' }, { value: '450325201', label: '白石乡' }, { value: '450325202', label: '华江瑶族乡' }] },
      { value: '450326', label: '永福县', children: [{ value: '450326100', label: '永福镇' }, { value: '450326101', label: '罗锦镇' }, { value: '450326102', label: '百寿镇' }, { value: '450326103', label: '苏桥镇' }, { value: '450326104', label: '三皇镇' }, { value: '450326105', label: '堡里镇' }, { value: '450326200', label: '龙江乡' }, { value: '450326201', label: '广福乡' }] },
      { value: '450327', label: '灌阳县', children: [{ value: '450327100', label: '灌阳镇' }, { value: '450327101', label: '黄关镇' }, { value: '450327102', label: '新街镇' }, { value: '450327103', label: '新圩镇' }, { value: '450327104', label: '文市镇' }, { value: '450327105', label: '水车镇' }, { value: '450327200', label: '西山瑶族乡' }, { value: '450327201', label: '观音阁乡' }, { value: '450327202', label: '洞井瑶族乡' }] },
      { value: '450328', label: '龙胜各族自治县', children: [{ value: '450328100', label: '龙胜镇' }, { value: '450328101', label: '瓢里镇' }, { value: '450328102', label: '三门镇' }, { value: '450328103', label: '龙脊镇' }, { value: '450328200', label: '平等镇' }, { value: '450328201', label: '泗水乡' }, { value: '450328202', label: '江底乡' }, { value: '450328203', label: '伟江乡' }, { value: '450328204', label: '乐江乡' }, { value: '450328205', label: '马堤乡' }] },
      { value: '450329', label: '资源县', children: [{ value: '450329100', label: '资源镇' }, { value: '450329101', label: '中峰镇' }, { value: '450329102', label: '梅溪镇' }, { value: '450329103', label: '瓜里乡' }, { value: '450329200', label: '车田苗族乡' }, { value: '450329201', label: '两水苗族乡' }, { value: '450329202', label: '河口瑶族乡' }] },
      { value: '450330', label: '平乐县', children: [{ value: '450330100', label: '平乐镇' }, { value: '450330101', label: '二塘镇' }, { value: '450330102', label: '沙子镇' }, { value: '450330103', label: '同安镇' }, { value: '450330104', label: '张家镇' }, { value: '450330105', label: '源头镇' }, { value: '450330200', label: '阳安乡' }, { value: '450330201', label: '青龙乡' }, { value: '450330202', label: '桥亭乡' }, { value: '450330203', label: '大发瑶族乡' }] },
      { value: '450332', label: '恭城瑶族自治县', children: [{ value: '450332100', label: '恭城镇' }, { value: '450332101', label: '栗木镇' }, { value: '450332102', label: '莲花镇' }, { value: '450332103', label: '平安镇' }, { value: '450332200', label: '三江乡' }, { value: '450332201', label: '嘉会镇' }, { value: '450332202', label: '西岭镇' }, { value: '450332203', label: '观音乡' }, { value: '450332204', label: '龙虎乡' }] },
      { value: '450381', label: '荔浦市', children: [{ value: '450381100', label: '荔城镇' }, { value: '450381101', label: '东昌镇' }, { value: '450381102', label: '新坪镇' }, { value: '450381103', label: '杜莫镇' }, { value: '450381104', label: '青山镇' }, { value: '450381105', label: '修仁镇' }, { value: '450381106', label: '大塘镇' }, { value: '450381107', label: '花篢镇' }, { value: '450381108', label: '双江镇' }, { value: '450381200', label: '马岭镇' }, { value: '450381201', label: '蒲芦瑶族乡' }, { value: '450381202', label: '茶城乡' }, { value: '450381203', label: '龙怀乡' }] }
    ]
  },
  {
    value: '450400',
    label: '梧州市',
    children: [
      { value: '450403', label: '万秀区', children: [{ value: '450403001', label: '城东街道' }, { value: '450403002', label: '城南街道' }, { value: '450403003', label: '城中街道' }, { value: '450403004', label: '城北街道' }, { value: '450403005', label: '角嘴街道' }, { value: '450403006', label: '东兴街道' }, { value: '450403100', label: '龙湖镇' }, { value: '450403101', label: '城东镇' }, { value: '450403102', label: '夏郢镇' }] },
      { value: '450405', label: '长洲区', children: [{ value: '450405001', label: '大塘街道' }, { value: '450405002', label: '兴龙街道' }, { value: '450405100', label: '长洲镇' }, { value: '450405101', label: '倒水镇' }] },
      { value: '450406', label: '龙圩区', children: [{ value: '450406100', label: '龙圩镇' }, { value: '450406101', label: '大坡镇' }, { value: '450406102', label: '广平镇' }, { value: '450406103', label: '新地镇' }] },
      { value: '450421', label: '苍梧县', children: [{ value: '450421100', label: '石桥镇' }, { value: '450421101', label: '沙头镇' }, { value: '450421102', label: '梨埠镇' }, { value: '450421103', label: '岭脚镇' }, { value: '450421104', label: '京南镇' }, { value: '450421105', label: '狮寨镇' }, { value: '450421106', label: '六堡镇' }, { value: '450421200', label: '旺甫镇' }, { value: '450421201', label: '木双镇' }] },
      { value: '450422', label: '藤县', children: [{ value: '450422100', label: '藤州镇' }, { value: '450422101', label: '塘步镇' }, { value: '450422102', label: '埌南镇' }, { value: '450422103', label: '同心镇' }, { value: '450422104', label: '金鸡镇' }, { value: '450422105', label: '新庆镇' }, { value: '450422106', label: '象棋镇' }, { value: '450422107', label: '岭景镇' }, { value: '450422108', label: '天平镇' }, { value: '450422109', label: '濛江镇' }, { value: '450422110', label: '和平镇' }, { value: '450422111', label: '太平镇' }, { value: '450422112', label: '古龙镇' }, { value: '450422200', label: '东荣镇' }, { value: '450422201', label: '大黎镇' }, { value: '450422202', label: '平福乡' }, { value: '450422203', label: '宁康乡' }] },
      { value: '450423', label: '蒙山县', children: [{ value: '450423100', label: '蒙山镇' }, { value: '450423101', label: '西河镇' }, { value: '450423102', label: '新圩镇' }, { value: '450423103', label: '文圩镇' }, { value: '450423104', label: '黄村镇' }, { value: '450423105', label: '陈塘镇' }, { value: '450423200', label: '汉豪乡' }, { value: '450423201', label: '长坪瑶族乡' }, { value: '450423202', label: '夏宜瑶族乡' }] },
      { value: '450481', label: '岑溪市', children: [{ value: '450481100', label: '岑城镇' }, { value: '450481101', label: '马路镇' }, { value: '450481102', label: '南渡镇' }, { value: '450481103', label: '水汶镇' }, { value: '450481104', label: '大隆镇' }, { value: '450481105', label: '梨木镇' }, { value: '450481106', label: '大业镇' }, { value: '450481107', label: '筋竹镇' }, { value: '450481108', label: '诚谏镇' }, { value: '450481109', label: '归义镇' }, { value: '450481110', label: '糯垌镇' }, { value: '450481111', label: '安平镇' }, { value: '450481112', label: '三堡镇' }, { value: '450481113', label: '波塘镇' }] }
    ]
  },
  {
    value: '450500',
    label: '北海市',
    children: [
      { value: '450502', label: '海城区', children: [{ value: '450502001', label: '中街街道' }, { value: '450502002', label: '东街街道' }, { value: '450502003', label: '西街街道' }, { value: '450502004', label: '海角街道' }, { value: '450502005', label: '地角街道' }, { value: '450502006', label: '高德街道' }, { value: '450502100', label: '驿马街道' }, { value: '450502101', label: '涠洲镇' }] },
      { value: '450503', label: '银海区', children: [{ value: '450503100', label: '福成镇' }, { value: '450503101', label: '平阳镇' }, { value: '450503102', label: '侨港镇' }, { value: '450503103', label: '银滩镇' }] },
      { value: '450512', label: '铁山港区', children: [{ value: '450512100', label: '南康镇' }, { value: '450512101', label: '营盘镇' }, { value: '450512102', label: '兴港镇' }] },
      { value: '450521', label: '合浦县', children: [{ value: '450521100', label: '廉州镇' }, { value: '450521101', label: '党江镇' }, { value: '450521102', label: '西场镇' }, { value: '450521103', label: '沙岗镇' }, { value: '450521104', label: '乌家镇' }, { value: '450521105', label: '闸口镇' }, { value: '450521106', label: '公馆镇' }, { value: '450521107', label: '白沙镇' }, { value: '450521108', label: '山口镇' }, { value: '450521109', label: '沙田镇' }, { value: '450521110', label: '石湾镇' }, { value: '450521111', label: '石康镇' }, { value: '450521112', label: '常乐镇' }, { value: '450521200', label: '曲樟乡' }, { value: '450521201', label: '星岛湖乡' }] }
    ]
  },
  {
    value: '450600',
    label: '防城港市',
    children: [
      { value: '450602', label: '港口区', children: [{ value: '450602001', label: '渔州坪街道' }, { value: '450602002', label: '白沙万街道' }, { value: '450602100', label: '企沙镇' }, { value: '450602101', label: '光坡镇' }, { value: '450602102', label: '公车镇' }, { value: '450602200', label: '王府街道' }] },
      { value: '450603', label: '防城区', children: [{ value: '450603100', label: '防城镇' }, { value: '450603101', label: '华石镇' }, { value: '450603102', label: '那梭镇' }, { value: '450603103', label: '那良镇' }, { value: '450603104', label: '峒中镇' }, { value: '450603105', label: '茅岭镇' }, { value: '450603106', label: '大菉镇' }, { value: '450603107', label: '扶隆镇' }, { value: '450603200', label: '滩营乡' }, { value: '450603201', label: '那垌乡' }, { value: '450603202', label: '十万山瑶族乡' }] },
      { value: '450621', label: '上思县', children: [{ value: '450621100', label: '思阳镇' }, { value: '450621101', label: '在妙镇' }, { value: '450621102', label: '叫安镇' }, { value: '450621200', label: '那琴乡' }, { value: '450621201', label: '平福乡' }, { value: '450621202', label: '华兰镇' }, { value: '450621203', label: '南屏瑶族乡' }] },
      { value: '450681', label: '东兴市', children: [{ value: '450681100', label: '东兴镇' }, { value: '450681101', label: '江平镇' }, { value: '450681102', label: '马路镇' }] }
    ]
  },
  {
    value: '450700',
    label: '钦州市',
    children: [
      { value: '450702', label: '钦南区', children: [{ value: '450702001', label: '向阳街道' }, { value: '450702002', label: '水东街道' }, { value: '450702003', label: '文峰街道' }, { value: '450702004', label: '南珠街道' }, { value: '450702005', label: '尖山街道' }, { value: '450702006', label: '沙埠镇' }, { value: '450702007', label: '康熙岭镇' }, { value: '450702008', label: '黄屋屯镇' }, { value: '450702100', label: '久隆镇' }, { value: '450702101', label: '东场镇' }, { value: '450702102', label: '那丽镇' }, { value: '450702103', label: '那彭镇' }, { value: '450702104', label: '那思镇' }, { value: '450702105', label: '大番坡镇' }, { value: '450702106', label: '龙门港镇' }, { value: '450702200', label: '犀牛脚镇' }] },
      { value: '450703', label: '钦北区', children: [{ value: '450703001', label: '长田街道' }, { value: '450703002', label: '鸿亭街道' }, { value: '450703003', label: '子材街道' }, { value: '450703100', label: '大垌镇' }, { value: '450703101', label: '平吉镇' }, { value: '450703102', label: '青塘镇' }, { value: '450703103', label: '小董镇' }, { value: '450703104', label: '板城镇' }, { value: '450703105', label: '那蒙镇' }, { value: '450703106', label: '长滩镇' }, { value: '450703107', label: '新棠镇' }, { value: '450703108', label: '大直镇' }, { value: '450703109', label: '大寺镇' }, { value: '450703110', label: '贵台镇' }] },
      { value: '450721', label: '灵山县', children: [{ value: '450721100', label: '灵城镇' }, { value: '450721101', label: '新圩镇' }, { value: '450721102', label: '丰塘镇' }, { value: '450721103', label: '平山镇' }, { value: '450721104', label: '石塘镇' }, { value: '450721105', label: '佛子镇' }, { value: '450721106', label: '平南镇' }, { value: '450721107', label: '烟墩镇' }, { value: '450721108', label: '檀圩镇' }, { value: '450721109', label: '那隆镇' }, { value: '450721110', label: '三隆镇' }, { value: '450721111', label: '陆屋镇' }, { value: '450721112', label: '旧州镇' }, { value: '450721113', label: '太平镇' }, { value: '450721114', label: '沙坪镇' }, { value: '450721115', label: '武利镇' }, { value: '450721116', label: '文利镇' }, { value: '450721117', label: '伯劳镇' }] },
      { value: '450722', label: '浦北县', children: [{ value: '450722100', label: '小江镇' }, { value: '450722101', label: '泉水镇' }, { value: '450722102', label: '石埇镇' }, { value: '450722103', label: '安石镇' }, { value: '450722104', label: '张黄镇' }, { value: '450722105', label: '大成镇' }, { value: '450722106', label: '白石水镇' }, { value: '450722107', label: '北通镇' }, { value: '450722108', label: '三合镇' }, { value: '450722109', label: '龙门镇' }, { value: '450722110', label: '福旺镇' }, { value: '450722111', label: '寨圩镇' }, { value: '450722112', label: '乐民镇' }, { value: '450722200', label: '六硍镇' }, { value: '450722201', label: '平睦镇' }, { value: '450722202', label: '官垌镇' }, { value: '450722203', label: '江城街道' }] }
    ]
  },
  {
    value: '450800',
    label: '贵港市',
    children: [
      { value: '450802', label: '港北区', children: [{ value: '450802001', label: '贵城街道' }, { value: '450802002', label: '港城街道' }, { value: '450802003', label: '郁江街道' }, { value: '450802100', label: '大圩镇' }, { value: '450802101', label: '庆丰镇' }, { value: '450802102', label: '根竹镇' }, { value: '450802103', label: '武乐镇' }, { value: '450802104', label: '中里乡' }, { value: '450802105', label: '奇石乡' }] },
      { value: '450803', label: '港南区', children: [{ value: '450803001', label: '江南街道' }, { value: '450803100', label: '桥圩镇' }, { value: '450803101', label: '木格镇' }, { value: '450803102', label: '木梓镇' }, { value: '450803103', label: '湛江镇' }, { value: '450803104', label: '东津镇' }, { value: '450803105', label: '八塘街道' }, { value: '450803106', label: '新塘镇' }, { value: '450803200', label: '瓦塘镇' }] },
      { value: '450804', label: '覃塘区', children: [{ value: '450804001', label: '覃塘街道' }, { value: '450804100', label: '东龙镇' }, { value: '450804101', label: '三里镇' }, { value: '450804102', label: '黄练镇' }, { value: '450804103', label: '石卡镇' }, { value: '450804104', label: '五里镇' }, { value: '450804105', label: '山北乡' }, { value: '450804106', label: '樟木镇' }, { value: '450804107', label: '大岭乡' }] },
      { value: '450821', label: '平南县', children: [{ value: '450821100', label: '平南镇' }, { value: '450821101', label: '平山镇' }, { value: '450821102', label: '寺面镇' }, { value: '450821103', label: '大新镇' }, { value: '450821104', label: '大坡镇' }, { value: '450821105', label: '六陈镇' }, { value: '450821106', label: '大安镇' }, { value: '450821107', label: '武林镇' }, { value: '450821108', label: '镇隆镇' }, { value: '450821109', label: '上渡镇' }, { value: '450821110', label: '大洲镇' }, { value: '450821111', label: '官成镇' }, { value: '450821112', label: '思旺镇' }, { value: '450821113', label: '东华镇' }, { value: '450821114', label: '大鹏镇' }, { value: '450821115', label: '安怀镇' }, { value: '450821116', label: '思界乡' }, { value: '450821117', label: '同和镇' }, { value: '450821118', label: '丹竹镇' }, { value: '450821119', label: '马练瑶族乡' }, { value: '450821200', label: '国安瑶族乡' }] },
      { value: '450881', label: '桂平市', children: [{ value: '450881001', label: '木乐镇' }, { value: '450881002', label: '木圭镇' }, { value: '450881003', label: '石咀镇' }, { value: '450881004', label: '油麻镇' }, { value: '450881005', label: '社坡镇' }, { value: '450881006', label: '罗秀镇' }, { value: '450881007', label: '麻垌镇' }, { value: '450881008', label: '社步镇' }, { value: '450881009', label: '下湾镇' }, { value: '450881010', label: '木根镇' }, { value: '450881011', label: '中沙镇' }, { value: '450881012', label: '大洋镇' }, { value: '450881013', label: '大湾镇' }, { value: '450881014', label: '白沙镇' }, { value: '450881015', label: '石龙镇' }, { value: '450881016', label: '蒙圩镇' }, { value: '450881017', label: '西山镇' }, { value: '450881018', label: '南木镇' }, { value: '450881019', label: '金田镇' }, { value: '450881020', label: '江口镇' }, { value: '450881021', label: '紫荆镇' }, { value: '450881022', label: '马皮乡' }, { value: '450881023', label: '罗播乡' }, { value: '450881024', label: '厚禄乡' }, { value: '450881025', label: '寻旺乡' }] }
    ]
  },
  {
    value: '450900',
    label: '玉林市',
    children: [
      { value: '450902', label: '玉州区', children: [{ value: '450902001', label: '玉城街道' }, { value: '450902002', label: '南江街道' }, { value: '450902003', label: '城西街道' }, { value: '450902004', label: '城北街道' }, { value: '450902005', label: '名山街道' }, { value: '450902100', label: '仁东镇' }, { value: '450902101', label: '仁厚镇' }, { value: '450902102', label: '大塘镇' }, { value: '450902103', label: '茂林镇' }] },
      { value: '450903', label: '福绵区', children: [{ value: '450903100', label: '福绵镇' }, { value: '450903101', label: '成均镇' }, { value: '450903102', label: '樟木镇' }, { value: '450903103', label: '新桥镇' }, { value: '450903104', label: '沙田镇' }, { value: '450903105', label: '石和镇' }] },
      { value: '450921', label: '容县', children: [{ value: '450921100', label: '容州镇' }, { value: '450921101', label: '杨梅镇' }, { value: '450921102', label: '灵山镇' }, { value: '450921103', label: '六王镇' }, { value: '450921104', label: '黎村镇' }, { value: '450921105', label: '杨村镇' }, { value: '450921106', label: '县底镇' }, { value: '450921107', label: '自良镇' }, { value: '450921108', label: '松山镇' }, { value: '450921109', label: '罗江镇' }, { value: '450921110', label: '石头镇' }, { value: '450921111', label: '石寨镇' }, { value: '450921112', label: '十里镇' }, { value: '450921113', label: '容西镇' }, { value: '450921114', label: '浪水镇' }] },
      { value: '450922', label: '陆川县', children: [{ value: '450922100', label: '温泉镇' }, { value: '450922101', label: '米场镇' }, { value: '450922102', label: '马坡镇' }, { value: '450922103', label: '珊罗镇' }, { value: '450922104', label: '平乐镇' }, { value: '450922105', label: '沙坡镇' }, { value: '450922106', label: '大桥镇' }, { value: '450922107', label: '横山镇' }, { value: '450922108', label: '古城镇' }, { value: '450922109', label: '乌石镇' }, { value: '450922110', label: '良田镇' }, { value: '450922111', label: '清湖镇' }, { value: '450922112', label: '沙湖镇' }, { value: '450922113', label: '滩面镇' }] },
      { value: '450923', label: '博白县', children: [{ value: '450923100', label: '博白镇' }, { value: '450923101', label: '双凤镇' }, { value: '450923102', label: '顿谷镇' }, { value: '450923103', label: '水鸣镇' }, { value: '450923104', label: '那林镇' }, { value: '450923105', label: '江宁镇' }, { value: '450923106', label: '三滩镇' }, { value: '450923107', label: '黄凌镇' }, { value: '450923108', label: '亚山镇' }, { value: '450923109', label: '旺茂镇' }, { value: '450923110', label: '东平镇' }, { value: '450923111', label: '沙河镇' }, { value: '450923112', label: '那卜镇' }, { value: '450923113', label: '文地镇' }, { value: '450923114', label: '英桥镇' }, { value: '450923115', label: '大坝镇' }, { value: '450923116', label: '菱角镇' }, { value: '450923117', label: '新田镇' }, { value: '450923118', label: '凤山镇' }, { value: '450923119', label: '宁潭镇' }, { value: '450923120', label: '大垌镇' }, { value: '450923121', label: '沙陂镇' }, { value: '450923122', label: '浪平镇' }, { value: '450923123', label: '松旺镇' }, { value: '450923200', label: '径口镇' }, { value: '450923201', label: '永安镇' }, { value: '450923202', label: '龙潭镇' }, { value: '450923203', label: '江宁镇' }] },
      { value: '450924', label: '兴业县', children: [{ value: '450924100', label: '石南镇' }, { value: '450924101', label: '葵阳镇' }, { value: '450924102', label: '山心镇' }, { value: '450924103', label: '大平山镇' }, { value: '450924104', label: '城隍镇' }, { value: '450924105', label: '沙塘镇' }, { value: '450924106', label: '蒲塘镇' }, { value: '450924107', label: '北市镇' }, { value: '450924108', label: '龙安镇' }, { value: '450924109', label: '高峰镇' }, { value: '450924110', label: '小平山镇' }, { value: '450924111', label: '卖酒镇' }, { value: '450924112', label: '洛阳镇' }] },
      { value: '450981', label: '北流市', children: [{ value: '450981001', label: '陵城街道' }, { value: '450981100', label: '北流镇' }, { value: '450981101', label: '新荣镇' }, { value: '450981102', label: '民安镇' }, { value: '450981103', label: '山围镇' }, { value: '450981104', label: '民乐镇' }, { value: '450981105', label: '西埌镇' }, { value: '450981106', label: '新圩镇' }, { value: '450981107', label: '大里镇' }, { value: '450981108', label: '塘岸镇' }, { value: '450981109', label: '清水口镇' }, { value: '450981110', label: '隆盛镇' }, { value: '450981111', label: '大坡外镇' }, { value: '450981112', label: '六靖镇' }, { value: '450981113', label: '新丰镇' }, { value: '450981114', label: '沙垌镇' }, { value: '450981115', label: '平政镇' }, { value: '450981116', label: '白马镇' }, { value: '450981117', label: '大伦镇' }, { value: '450981118', label: '扶新镇' }, { value: '450981119', label: '六麻镇' }, { value: '450981120', label: '清湾镇' }, { value: '450981121', label: '石窝镇' }] }
    ]
  },
  {
    value: '451000',
    label: '百色市',
    children: [
      { value: '451002', label: '右江区', children: [{ value: '451002001', label: '百城街道' }, { value: '451002002', label: '龙景街道' }, { value: '451002003', label: '向阳街道' }, { value: '451002004', label: '四塘镇' }, { value: '451002005', label: '阳圩镇' }, { value: '451002006', label: '汪甸瑶族乡' }, { value: '451002007', label: '大楞乡' }, { value: '451002008', label: '龙川镇' }, { value: '451002009', label: '永乐镇' }] },
      { value: '451003', label: '田阳区', children: [{ value: '451003100', label: '田州镇' }, { value: '451003101', label: '那坡镇' }, { value: '451003102', label: '坡洪镇' }, { value: '451003103', label: '那满镇' }, { value: '451003104', label: '百育镇' }, { value: '451003105', label: '洞靖镇' }, { value: '451003106', label: '头塘镇' }, { value: '451003107', label: '巴别乡' }, { value: '451003108', label: '五村镇' }, { value: '451003109', label: '玉凤镇' }] },
      { value: '451022', label: '田东县', children: [{ value: '451022100', label: '平马镇' }, { value: '451022101', label: '祥周镇' }, { value: '451022102', label: '林逢镇' }, { value: '451022103', label: '思林镇' }, { value: '451022104', label: '作登瑶族乡' }, { value: '451022105', label: '印茶镇' }, { value: '451022106', label: '江城镇' }, { value: '451022107', label: '朔良镇' }, { value: '451022108', label: '义圩镇' }, { value: '451022109', label: '那拔镇' }] },
      { value: '451024', label: '德保县', children: [{ value: '451024100', label: '城关镇' }, { value: '451024101', label: '隆桑镇' }, { value: '451024102', label: '敬德镇' }, { value: '451024103', label: '足荣镇' }, { value: '451024104', label: '马隘镇' }, { value: '451024105', label: '都安乡' }, { value: '451024106', label: '荣华乡' }, { value: '451024107', label: '东凌镇' }, { value: '451024108', label: '燕峒乡' }, { value: '451024109', label: '那甲镇' }, { value: '451024110', label: '巴头乡' }, { value: '451024111', label: '龙光乡' }] },
      { value: '451026', label: '那坡县', children: [{ value: '451026100', label: '城厢镇' }, { value: '451026101', label: '平孟镇' }, { value: '451026102', label: '坡荷镇' }, { value: '451026103', label: '龙合镇' }, { value: '451026104', label: '百省乡' }, { value: '451026105', label: '百合乡' }, { value: '451026106', label: '百南乡' }, { value: '451026107', label: '德隆乡' }, { value: '451026108', label: '百都乡' }] },
      { value: '451027', label: '凌云县', children: [{ value: '451027100', label: '泗城镇' }, { value: '451027101', label: '逻楼镇' }, { value: '451027102', label: '加尤镇' }, { value: '451027103', label: '下甲镇' }, { value: '451027200', label: '伶站瑶族乡' }, { value: '451027201', label: '朝里瑶族乡' }, { value: '451027202', label: '沙里瑶族乡' }, { value: '451027203', label: '玉洪瑶族乡' }] },
      { value: '451028', label: '乐业县', children: [{ value: '451028100', label: '同乐镇' }, { value: '451028101', label: '甘田镇' }, { value: '451028102', label: '新化镇' }, { value: '451028103', label: '花坪镇' }, { value: '451028200', label: '逻沙乡' }, { value: '451028201', label: '逻西乡' }, { value: '451028202', label: '幼平乡' }, { value: '451028203', label: '雅长乡' }] },
      { value: '451029', label: '田林县', children: [{ value: '451029100', label: '乐里镇' }, { value: '451029101', label: '旧州镇' }, { value: '451029102', label: '定安镇' }, { value: '451029103', label: '六隆镇' }, { value: '451029104', label: '潭洞乡' }, { value: '451029105', label: '浪平镇' }, { value: '451029106', label: '八桂瑶族乡' }, { value: '451029107', label: '八渡瑶族乡' }, { value: '451029108', label: '平塘乡' }, { value: '451029109', label: '利周瑶族乡' }, { value: '451029110', label: '百乐乡' }, { value: '451029111', label: '那比乡' }, { value: '451029112', label: '高龙乡' }, { value: '451029113', label: '者苗乡' }] },
      { value: '451030', label: '西林县', children: [{ value: '451030100', label: '八达镇' }, { value: '451030101', label: '古障镇' }, { value: '451030102', label: '马蚌镇' }, { value: '451030103', label: '那劳镇' }, { value: '451030200', label: '普合苗族乡' }, { value: '451030201', label: '西平乡' }, { value: '451030202', label: '那佐苗族乡' }, { value: '451030203', label: '足别瑶族苗族乡' }] },
      { value: '451031', label: '隆林各族自治县', children: [{ value: '451031100', label: '新州镇' }, { value: '451031101', label: '桠杈镇' }, { value: '451031102', label: '天生桥镇' }, { value: '451031103', label: '平班镇' }, { value: '451031200', label: '德峨镇' }, { value: '451031201', label: '沙梨乡' }, { value: '451031202', label: '岩茶乡' }, { value: '451031203', label: '者浪乡' }, { value: '451031204', label: '者保乡' }, { value: '451031205', label: '革步乡' }, { value: '451031206', label: '金钟山乡' }, { value: '451031207', label: '猪场乡' }, { value: '451031208', label: '蛇场乡' }, { value: '451031209', label: '克长乡' }, { value: '451031210', label: '介廷乡' }, { value: '451031211', label: '隆或镇' }] },
      { value: '451081', label: '靖西市', children: [{ value: '451081100', label: '新靖镇' }, { value: '451081101', label: '化峒镇' }, { value: '451081102', label: '新甲乡' }, { value: '451081103', label: '龙临镇' }, { value: '451081104', label: '渠洋镇' }, { value: '451081105', label: '岳圩镇' }, { value: '451081106', label: '龙邦镇' }, { value: '451081107', label: '湖润镇' }, { value: '451081108', label: '安德镇' }, { value: '451081109', label: '地州镇' }, { value: '451081110', label: '禄峒镇' }, { value: '451081111', label: '同德乡' }, { value: '451081112', label: '壬庄乡' }, { value: '451081113', label: '安宁乡' }, { value: '451081114', label: '果乐乡' }, { value: '451081115', label: '南坡乡' }, { value: '451081116', label: '吞盘乡' }, { value: '451081117', label: '魁圩乡' }, { value: '451081118', label: '武平镇' }] },
      { value: '451082', label: '平果市', children: [{ value: '451082100', label: '马头镇' }, { value: '451082101', label: '新安镇' }, { value: '451082102', label: '果化镇' }, { value: '451082103', label: '太平镇' }, { value: '451082104', label: '坡造镇' }, { value: '451082105', label: '四塘镇' }, { value: '451082106', label: '旧城镇' }, { value: '451082107', label: '海城乡' }, { value: '451082108', label: '凤梧镇' }, { value: '451082109', label: '榜圩镇' }, { value: '451082110', label: '同老乡' }, { value: '451082111', label: '黎明乡' }] }
    ]
  },
  {
    value: '451100',
    label: '贺州市',
    children: [
      { value: '451102', label: '八步区', children: [{ value: '451102001', label: '八步街道' }, { value: '451102002', label: '城东街道' }, { value: '451102003', label: '江南街道' }, { value: '451102100', label: '贺街镇' }, { value: '451102101', label: '步头镇' }, { value: '451102102', label: '莲塘镇' }, { value: '451102103', label: '大宁镇' }, { value: '451102104', label: '南乡镇' }, { value: '451102105', label: '桂岭镇' }, { value: '451102106', label: '开山镇' }, { value: '451102107', label: '里松镇' }, { value: '451102108', label: '仁义镇' }, { value: '451102109', label: '信都镇' }, { value: '451102110', label: '灵峰镇' }, { value: '451102111', label: '铺门镇' }] },
      { value: '451103', label: '平桂区', children: [{ value: '451103001', label: '西湾街道' }, { value: '451103100', label: '黄田镇' }, { value: '451103101', label: '鹅塘镇' }, { value: '451103102', label: '沙田镇' }, { value: '451103103', label: '公会镇' }, { value: '451103104', label: '水口镇' }, { value: '451103105', label: '望高镇' }, { value: '451103106', label: '羊头镇' }, { value: '451103200', label: '大平瑶族乡' }] },
      { value: '451121', label: '昭平县', children: [{ value: '451121100', label: '昭平镇' }, { value: '451121101', label: '文竹镇' }, { value: '451121102', label: '黄姚镇' }, { value: '451121103', label: '富罗镇' }, { value: '451121104', label: '北陀镇' }, { value: '451121105', label: '马江镇' }, { value: '451121106', label: '五将镇' }, { value: '451121107', label: '仙回瑶族乡' }, { value: '451121108', label: '走马镇' }, { value: '451121109', label: '凤凰乡' }, { value: '451121110', label: '木格乡' }, { value: '451121111', label: '樟木林镇' }] },
      { value: '451122', label: '钟山县', children: [{ value: '451122100', label: '钟山镇' }, { value: '451122101', label: '回龙镇' }, { value: '451122102', label: '石龙镇' }, { value: '451122103', label: '凤翔镇' }, { value: '451122104', label: '珊瑚镇' }, { value: '451122105', label: '同古镇' }, { value: '451122106', label: '公安镇' }, { value: '451122107', label: '燕塘镇' }, { value: '451122108', label: '清塘镇' }, { value: '451122109', label: '红花镇' }, { value: '451122200', label: '两安瑶族乡' }, { value: '451122201', label: '花山瑶族乡' }] },
      { value: '451123', label: '富川瑶族自治县', children: [{ value: '451123100', label: '富阳镇' }, { value: '451123101', label: '白沙镇' }, { value: '451123102', label: '莲山镇' }, { value: '451123103', label: '葛坡镇' }, { value: '451123104', label: '麦岭镇' }, { value: '451123105', label: '古城镇' }, { value: '451123106', label: '福利镇' }, { value: '451123107', label: '城北镇' }, { value: '451123108', label: '新华乡' }, { value: '451123200', label: '朝东镇' }, { value: '451123201', label: '石家乡' }, { value: '451123202', label: '柳家乡' }] }
    ]
  },
  {
    value: '451200',
    label: '河池市',
    children: [
      { value: '451202', label: '金城江区', children: [{ value: '451202001', label: '金城江街道' }, { value: '451202002', label: '东江镇' }, { value: '451202003', label: '六圩镇' }, { value: '451202004', label: '五圩镇' }, { value: '451202005', label: '九圩镇' }, { value: '451202006', label: '六甲镇' }, { value: '451202007', label: '河池镇' }, { value: '451202008', label: '拔贡镇' }, { value: '451202009', label: '白土乡' }, { value: '451202010', label: '保平乡' }, { value: '451202011', label: '长老乡' }, { value: '451202012', label: '侧岭乡' }] },
      { value: '451203', label: '宜州区', children: [{ value: '451203100', label: '庆远镇' }, { value: '451203101', label: '三岔镇' }, { value: '451203102', label: '洛西镇' }, { value: '451203103', label: '怀远镇' }, { value: '451203104', label: '德胜镇' }, { value: '451203105', label: '石别镇' }, { value: '451203106', label: '北山镇' }, { value: '451203107', label: '祥贝乡' }, { value: '451203108', label: '刘三姐镇' }, { value: '451203109', label: '洛东镇' }, { value: '451203110', label: '福龙瑶族乡' }, { value: '451203111', label: '北牙瑶族乡' }, { value: '451203112', label: '屏南乡' }, { value: '451203113', label: '同德乡' }, { value: '451203114', label: '安马乡' }, { value: '451203115', label: '龙头乡' }] },
      { value: '451221', label: '南丹县', children: [{ value: '451221100', label: '城关镇' }, { value: '451221101', label: '大厂镇' }, { value: '451221102', label: '车河镇' }, { value: '451221103', label: '芒场镇' }, { value: '451221104', label: '六寨镇' }, { value: '451221105', label: '月里镇' }, { value: '451221106', label: '吾隘镇' }, { value: '451221107', label: '罗富镇' }, { value: '451221108', label: '中堡苗族乡' }, { value: '451221109', label: '八圩瑶族乡' }, { value: '451221110', label: '里湖瑶族乡' }] },
      { value: '451222', label: '天峨县', children: [{ value: '451222100', label: '六排镇' }, { value: '451222101', label: '岜暮乡' }, { value: '451222102', label: '纳直乡' }, { value: '451222103', label: '向阳镇' }, { value: '451222104', label: '八腊瑶族乡' }, { value: '451222105', label: '老鹏乡' }, { value: '451222106', label: '更新乡' }, { value: '451222107', label: '坡结乡' }, { value: '451222108', label: '下老乡' }, { value: '451222109', label: '三堡乡' }] },
      { value: '451223', label: '凤山县', children: [{ value: '451223100', label: '凤城镇' }, { value: '451223101', label: '袍里乡' }, { value: '451223102', label: '砦牙乡' }, { value: '451223103', label: '长洲镇' }, { value: '451223104', label: '金牙瑶族乡' }, { value: '451223105', label: '乔音乡' }, { value: '451223106', label: '平乐瑶族乡' }, { value: '451223107', label: '中亭乡' }, { value: '451223108', label: '江洲瑶族乡' }] },
      { value: '451224', label: '东兰县', children: [{ value: '451224100', label: '东兰镇' }, { value: '451224101', label: '隘洞镇' }, { value: '451224102', label: '长乐镇' }, { value: '451224103', label: '三石镇' }, { value: '451224104', label: '武篆镇' }, { value: '451224105', label: '泗孟乡' }, { value: '451224106', label: '兰木乡' }, { value: '451224107', label: '长江镇' }, { value: '451224108', label: '巴畴乡' }, { value: '451224109', label: '金谷乡' }, { value: '451224110', label: '三弄瑶族乡' }, { value: '451224111', label: '花香乡' }, { value: '451224112', label: '大同乡' }, { value: '451224113', label: '切学乡' }] },
      { value: '451225', label: '罗城仫佬族自治县', children: [{ value: '451225100', label: '东门镇' }, { value: '451225101', label: '龙岸镇' }, { value: '451225102', label: '黄金镇' }, { value: '451225103', label: '小长安镇' }, { value: '451225104', label: '四把镇' }, { value: '451225105', label: '天河镇' }, { value: '451225200', label: '怀群镇' }, { value: '451225201', label: '纳翁乡' }, { value: '451225202', label: '乔善乡' }, { value: '451225203', label: '兼爱乡' }, { value: '451225204', label: '宝坛乡' }] },
      { value: '451226', label: '环江毛南族自治县', children: [{ value: '451226100', label: '思恩镇' }, { value: '451226101', label: '水源镇' }, { value: '451226102', label: '洛阳镇' }, { value: '451226103', label: '川山镇' }, { value: '451226104', label: '明伦镇' }, { value: '451226105', label: '东兴镇' }, { value: '451226106', label: '大安乡' }, { value: '451226107', label: '长美乡' }, { value: '451226108', label: '龙岩乡' }, { value: '451226109', label: '驯乐苗族乡' }, { value: '451226110', label: '大才乡' }, { value: '451226200', label: '下南乡' }] },
      { value: '451227', label: '巴马瑶族自治县', children: [{ value: '451227100', label: '巴马镇' }, { value: '451227101', label: '燕洞镇' }, { value: '451227102', label: '甲篆镇' }, { value: '451227103', label: '那社乡' }, { value: '451227104', label: '所略乡' }, { value: '451227105', label: '西山乡' }, { value: '451227106', label: '东山乡' }, { value: '451227200', label: '凤凰乡' }, { value: '451227201', label: '那桃乡' }, { value: '451227202', label: '百林乡' }] },
      { value: '451228', label: '都安瑶族自治县', children: [{ value: '451228100', label: '安阳镇' }, { value: '451228101', label: '高岭镇' }, { value: '451228102', label: '澄江镇' }, { value: '451228103', label: '地苏镇' }, { value: '451228104', label: '东庙乡' }, { value: '451228105', label: '大兴镇' }, { value: '451228106', label: '板岭乡' }, { value: '451228107', label: '永安镇' }, { value: '451228108', label: '三只羊乡' }, { value: '451228109', label: '下坳镇' }, { value: '451228110', label: '隆福乡' }, { value: '451228111', label: '保安乡' }, { value: '451228112', label: '龙湾乡' }, { value: '451228113', label: '菁盛乡' }, { value: '451228114', label: '百旺镇' }, { value: '451228115', label: '拉烈镇' }, { value: '451228116', label: '加贵乡' }, { value: '451228200', label: '拉仁镇' }, { value: '451228201', label: '九渡乡' }, { value: '451228202', label: '五竹乡' }] },
      { value: '451229', label: '大化瑶族自治县', children: [{ value: '451229100', label: '大化镇' }, { value: '451229101', label: '都阳镇' }, { value: '451229102', label: '岩滩镇' }, { value: '451229103', label: '共和乡' }, { value: '451229104', label: '贡川乡' }, { value: '451229105', label: '百马乡' }, { value: '451229106', label: '古河乡' }, { value: '451229107', label: '古文乡' }, { value: '451229108', label: '江南乡' }, { value: '451229109', label: '羌圩乡' }, { value: '451229110', label: '乙圩乡' }, { value: '451229111', label: '北景乡' }, { value: '451229112', label: '板升乡' }, { value: '451229113', label: '七百弄乡' }, { value: '451229114', label: '雅龙乡' }, { value: '451229115', label: '六也乡' }] }
    ]
  },
  {
    value: '451300',
    label: '来宾市',
    children: [
      { value: '451302', label: '兴宾区', children: [{ value: '451302001', label: '城东街道' }, { value: '451302002', label: '城北街道' }, { value: '451302003', label: '河西街道' }, { value: '451302100', label: '凤凰镇' }, { value: '451302101', label: '良江镇' }, { value: '451302102', label: '小平阳镇' }, { value: '451302103', label: '迁江镇' }, { value: '451302104', label: '石陵镇' }, { value: '451302105', label: '桥巩镇' }, { value: '451302106', label: '寺山镇' }, { value: '451302107', label: '七洞乡' }, { value: '451302108', label: '陶邓镇' }, { value: '451302109', label: '三五镇' }, { value: '451302110', label: '五山镇' }, { value: '451302111', label: '石牙镇' }, { value: '451302112', label: '平阳镇' }, { value: '451302113', label: '良塘乡' }, { value: '451302114', label: '蒙村镇' }, { value: '451302115', label: '南泗乡' }, { value: '451302116', label: '高安乡' }] },
      { value: '451321', label: '忻城县', children: [{ value: '451321100', label: '城关镇' }, { value: '451321101', label: '大塘镇' }, { value: '451321102', label: '思练镇' }, { value: '451321103', label: '红渡镇' }, { value: '451321104', label: '古蓬镇' }, { value: '451321105', label: '马泗乡' }, { value: '451321106', label: '欧洞乡' }, { value: '451321107', label: '北更乡' }, { value: '451321108', label: '新圩乡' }, { value: '451321109', label: '遂意乡' }, { value: '451321110', label: '果遂镇' }] },
      { value: '451322', label: '象州县', children: [{ value: '451322100', label: '象州镇' }, { value: '451322101', label: '石龙镇' }, { value: '451322102', label: '运江镇' }, { value: '451322103', label: '寺村镇' }, { value: '451322104', label: '中平镇' }, { value: '451322105', label: '罗秀镇' }, { value: '451322106', label: '大乐镇' }, { value: '451322107', label: '马坪镇' }, { value: '451322108', label: '百丈乡' }, { value: '451322109', label: '妙皇乡' }, { value: '451322110', label: '水晶乡' }] },
      { value: '451323', label: '武宣县', children: [{ value: '451323100', label: '武宣镇' }, { value: '451323101', label: '桐岭镇' }, { value: '451323102', label: '通挽镇' }, { value: '451323103', label: '东乡镇' }, { value: '451323104', label: '三里镇' }, { value: '451323105', label: '二塘镇' }, { value: '451323106', label: '禄新镇' }, { value: '451323107', label: '思灵镇' }, { value: '451323200', label: '黄茆镇' }, { value: '451323201', label: '金鸡乡' }] },
      { value: '451324', label: '金秀瑶族自治县', children: [{ value: '451324100', label: '金秀镇' }, { value: '451324101', label: '桐木镇' }, { value: '451324102', label: '头排镇' }, { value: '451324103', label: '三角乡' }, { value: '451324104', label: '忠良乡' }, { value: '451324105', label: '罗香乡' }, { value: '451324106', label: '长垌乡' }, { value: '451324107', label: '大樟乡' }, { value: '451324108', label: '六巷乡' }, { value: '451324109', label: '三江乡' }] },
      { value: '451381', label: '合山市', children: [{ value: '451381001', label: '岭南镇' }, { value: '451381002', label: '北泗镇' }, { value: '451381003', label: '河里镇' }] }
    ]
  },
  {
    value: '451400',
    label: '崇左市',
    children: [
      { value: '451402', label: '江州区', children: [{ value: '451402001', label: '江州镇' }, { value: '451402002', label: '新和镇' }, { value: '451402003', label: '濑湍镇' }, { value: '451402004', label: '驮卢镇' }, { value: '451402005', label: '左州镇' }, { value: '451402006', label: '太平镇' }, { value: '451402007', label: '那隆镇' }, { value: '451402008', label: '罗白乡' }, { value: '451402009', label: '板利乡' }] },
      { value: '451421', label: '扶绥县', children: [{ value: '451421100', label: '新宁镇' }, { value: '451421101', label: '渠黎镇' }, { value: '451421102', label: '渠旧镇' }, { value: '451421103', label: '山圩镇' }, { value: '451421104', label: '柳桥镇' }, { value: '451421105', label: '东门镇' }, { value: '451421106', label: '东罗镇' }, { value: '451421107', label: '中东镇' }, { value: '451421108', label: '昌平乡' }, { value: '451421109', label: '岜盆乡' }] },
      { value: '451422', label: '宁明县', children: [{ value: '451422100', label: '城中镇' }, { value: '451422101', label: '爱店镇' }, { value: '451422102', label: '明江镇' }, { value: '451422103', label: '海渊镇' }, { value: '451422104', label: '亭亮镇' }, { value: '451422105', label: '桐棉镇' }, { value: '451422106', label: '那堪镇' }, { value: '451422107', label: '寨安乡' }, { value: '451422108', label: '峙浪乡' }, { value: '451422109', label: '那楠乡' }, { value: '451422110', label: '东安乡' }, { value: '451422200', label: '板棍乡' }, { value: '451422201', label: '北江乡' }] },
      { value: '451423', label: '龙州县', children: [{ value: '451423100', label: '龙州镇' }, { value: '451423101', label: '下冻镇' }, { value: '451423102', label: '水口镇' }, { value: '451423103', label: '金龙镇' }, { value: '451423104', label: '响水镇' }, { value: '451423105', label: '八角乡' }, { value: '451423106', label: '逐卜乡' }, { value: '451423107', label: '上降乡' }, { value: '451423108', label: '彬桥乡' }, { value: '451423109', label: '武德乡' }, { value: '451423110', label: '上龙乡' }, { value: '451423111', label: '里建乡' }] },
      { value: '451424', label: '大新县', children: [{ value: '451424100', label: '桃城镇' }, { value: '451424101', label: '全茗镇' }, { value: '451424102', label: '雷平镇' }, { value: '451424103', label: '硕龙镇' }, { value: '451424104', label: '下雷镇' }, { value: '451424105', label: '五山乡' }, { value: '451424106', label: '龙门乡' }, { value: '451424107', label: '昌明乡' }, { value: '451424108', label: '福隆乡' }, { value: '451424109', label: '那岭乡' }, { value: '451424110', label: '恩城乡' }, { value: '451424111', label: '榄圩乡' }, { value: '451424112', label: '宝圩乡' }, { value: '451424113', label: '堪圩乡' }] },
      { value: '451425', label: '天等县', children: [{ value: '451425100', label: '天等镇' }, { value: '451425101', label: '龙茗镇' }, { value: '451425102', label: '进结镇' }, { value: '451425103', label: '向都镇' }, { value: '451425104', label: '东平镇' }, { value: '451425105', label: '福新镇' }, { value: '451425106', label: '把荷乡' }, { value: '451425107', label: '驮堪乡' }, { value: '451425108', label: '进远乡' }, { value: '451425109', label: '上映乡' }, { value: '451425110', label: '宁干乡' }, { value: '451425111', label: '小山乡' }, { value: '451425112', label: '都康乡' }] },
      { value: '451481', label: '凭祥市', children: [{ value: '451481100', label: '凭祥镇' }, { value: '451481101', label: '友谊镇' }, { value: '451481102', label: '上石镇' }, { value: '451481103', label: '夏石镇' }] }
    ]
  }
]

module.exports = guangxiRegions
