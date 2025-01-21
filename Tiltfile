load('ext://namespace', 'namespace_create')
# appending '-dev' just in case this is ever ran from prod cluster
namespace = 'plant-lab-dev'
namespace_create(namespace)

load('ext://dotenv', 'dotenv')
# load env vars from .env
dotenv()

docker_build(
    'plant-lab',
    context='.'
)

# create fcm app
k8s_yaml(
    helm(
        'charts/plant-lab',
        name='plat-lab',
        namespace=namespace,
        # using set instead of values, for now? for ever?
        #values=['path/to'],
        set=[
            'image.name=plant-lab',
            'namespace={}'.format(namespace),
            'db.url={}'.format(os.getenv('DB_URL')),
            'db.user={}'.format(os.getenv('DB_USER')),
            'db.pass={}'.format(os.getenv('DB_PASS'))
        ]
    )
)